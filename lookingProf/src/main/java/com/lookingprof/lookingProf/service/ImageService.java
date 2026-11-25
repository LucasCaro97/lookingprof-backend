package com.lookingprof.lookingProf.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final Path uploadDir;

    public ImageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de imágenes: " + this.uploadDir, e);
        }
    }

    public String copyProfileImg(String oldImageUrl, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("La imagen está vacía");
        }

        String extension = extensionOf(image.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Formato de imagen no permitido");
        }

        String photoName = UUID.randomUUID() + "." + extension;
        Path photoPath = resolveSafe(photoName);
        try {
            Files.copy(image.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
            deleteIfExists(oldImageUrl);
            return photoName;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error al guardar la imagen");
        }
    }

    public String storeBytes(byte[] bytes, String extension) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("La imagen está vacía");
        }
        String normalized = extension == null ? "" : extension.toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(normalized)) {
            throw new IllegalArgumentException("Formato de imagen no permitido");
        }
        String photoName = UUID.randomUUID() + "." + normalized;
        try {
            Files.write(resolveSafe(photoName), bytes);
            return photoName;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error al guardar la imagen");
        }
    }

    public byte[] readImage(String imageName) throws IOException {
        Path imagePath = resolveSafe(imageName);
        if (!Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
            throw new NoSuchFileException(imageName);
        }
        return Files.readAllBytes(imagePath);
    }

    public MediaType mediaTypeOf(String imageName) {
        String extension = extensionOf(imageName);
        return switch (extension) {
            case "png" -> MediaType.IMAGE_PNG;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.IMAGE_JPEG;
        };
    }

    private void deleteIfExists(String imageName) {
        if (imageName == null || imageName.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(resolveSafe(imageName));
        } catch (Exception ignored) {
            // no bloquear la subida si no se puede borrar la imagen anterior
        }
    }

    private Path resolveSafe(String imageName) {
        if (imageName == null || imageName.isBlank() || imageName.contains("..") || imageName.contains("/") || imageName.contains("\\")) {
            throw new IllegalArgumentException("Nombre de imagen inválido");
        }
        Path resolved = uploadDir.resolve(imageName).normalize();
        if (!resolved.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Nombre de imagen inválido");
        }
        return resolved;
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
