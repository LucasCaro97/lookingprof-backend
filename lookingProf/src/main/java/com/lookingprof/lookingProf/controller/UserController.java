package com.lookingprof.lookingProf.controller;

import com.lookingprof.lookingProf.config.CurrentUserService;
import com.lookingprof.lookingProf.dto.ContactResponseDTO;
import com.lookingprof.lookingProf.dto.ProfessionalPublicDTO;
import com.lookingprof.lookingProf.dto.UserRequestDTO;
import com.lookingprof.lookingProf.dto.UserResponseDTO;
import com.lookingprof.lookingProf.service.ImageService;
import com.lookingprof.lookingProf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final CurrentUserService currentUserService;

    @GetMapping("/all")
    public ResponseEntity<List<ProfessionalPublicDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.listPublicProfessionals());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        String response = userService.deleteUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,
                                        @RequestParam(value = "phone", required = false) String phone,
                                        @RequestParam(value = "province", required = false) Integer province,
                                        @RequestParam(value = "city", required = false) Integer city,
                                        @RequestParam(value = "profession", required = false) Integer profession,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        currentUserService.assertOwnerOrAdmin(id);
        UserRequestDTO userRequestDTO = new UserRequestDTO(phone, province, city, profession, description, image);
        Optional<UserResponseDTO> updatedUser = userService.updateUser(id, userRequestDTO);
        if (updatedUser.isPresent()) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se ha encontrado el usuario");
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws IOException {
        try {
            byte[] imageBytes = imageService.readImage(imageName);
            return ResponseEntity.ok().contentType(imageService.mediaTypeOf(imageName)).body(imageBytes);
        } catch (NoSuchFileException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/contact")
    public ResponseEntity<ContactResponseDTO> getContact(@PathVariable int id) {
        return ResponseEntity.ok(userService.getContact(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable int id) {
        if (currentUserService.isAuthenticatedUser()) {
            Optional<UserResponseDTO> userResponseDTO = userService.findById(id);
            if (userResponseDTO.isPresent()) {
                return ResponseEntity.ok(userResponseDTO.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron usuarios");
        }
        Optional<ProfessionalPublicDTO> publicProfile = userService.findPublicById(id);
        if (publicProfile.isPresent()) {
            return ResponseEntity.ok(publicProfile.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron usuarios");
    }

    @GetMapping("/firstName")
    public ResponseEntity<?> findByFirstname(@RequestParam String firstName) {
        Optional<List<UserResponseDTO>> optionalUsers = userService.findByFirstname(firstName);
        if (optionalUsers.isPresent()) {
            return ResponseEntity.ok(optionalUsers.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron usuarios");
    }

    @GetMapping("/email")
    public ResponseEntity<?> findByEmail(@RequestParam String email) {
        Optional<UserResponseDTO> optionalUsers = userService.findByEmail(email);
        if (optionalUsers.isPresent()) {
            return ResponseEntity.ok(optionalUsers.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron usuarios");
    }

    @GetMapping("/province")
    public ResponseEntity<List<ProfessionalPublicDTO>> findByProvince(@RequestParam String province) {
        return ResponseEntity.ok(userService.searchProfessionals(
                null, province, null, null, null, null, PageRequest.of(0, 50)
        ).getContent());
    }

    @GetMapping("/city")
    public ResponseEntity<List<ProfessionalPublicDTO>> findByCity(@RequestParam String city) {
        return ResponseEntity.ok(userService.searchProfessionals(
                null, null, city, null, null, null, PageRequest.of(0, 50)
        ).getContent());
    }

    @GetMapping("/profession")
    public ResponseEntity<List<ProfessionalPublicDTO>> findByProfession(@RequestParam String profession) {
        return ResponseEntity.ok(userService.searchProfessionals(
                profession, null, null, null, null, null, PageRequest.of(0, 50)
        ).getContent());
    }

    @GetMapping("/qualification/{qualification}")
    public ResponseEntity<?> findByQualification(@PathVariable int qualification) {
        Optional<List<UserResponseDTO>> optionalUsers = userService.findByQualification(qualification);
        if (optionalUsers.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<ProfessionalPublicDTO> publicList = optionalUsers.get().stream()
                .map(dto -> userService.findPublicById(dto.getIdUser()).orElse(null))
                .filter(item -> item != null)
                .toList();
        return ResponseEntity.ok(publicList);
    }

    @GetMapping("/allActive")
    public ResponseEntity<List<ProfessionalPublicDTO>> getAllUsersActives() {
        return ResponseEntity.ok(userService.listPublicProfessionals());
    }
}
