package com.lookingprof.lookingProf.seed;

import com.lookingprof.lookingProf.model.City;
import com.lookingprof.lookingProf.model.Enum.Role;
import com.lookingprof.lookingProf.model.Profession;
import com.lookingprof.lookingProf.model.Province;
import com.lookingprof.lookingProf.model.User;
import com.lookingprof.lookingProf.repository.ICityRepository;
import com.lookingprof.lookingProf.repository.IProfessionRepository;
import com.lookingprof.lookingProf.repository.IProvincesRepository;
import com.lookingprof.lookingProf.repository.IUserRepository;
import com.lookingprof.lookingProf.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(2)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.demo-users", havingValue = "true")
public class DevUserSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevUserSeedRunner.class);
    static final String DEMO_USER_EMAIL = "usuario@lookingprof.dev";

    private final SeedProperties seedProperties;
    private final IUserRepository userRepository;
    private final IProfessionRepository professionRepository;
    private final IProvincesRepository provincesRepository;
    private final ICityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final RestClient avatarClient = avatarClient();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail(DEMO_USER_EMAIL).isPresent()) {
            int filled = fillMissingAvatars();
            log.info("Usuarios demo ya existen. Avatares nuevos: {}", filled);
            return;
        }
        List<City> cities = cityRepository.findAll();
        List<Profession> professions = professionRepository.findAll();
        if (cities.isEmpty() || professions.isEmpty()) {
            log.warn("No hay catálogo suficiente para generar usuarios demo");
            return;
        }

        String encoded = passwordEncoder.encode(seedProperties.getDemoPassword());
        saveUser("Lucía", "Fernández", DEMO_USER_EMAIL, encoded, Role.USER,
                cityNamed("Córdoba", "Córdoba"), null, null, null);

        AtomicInteger created = new AtomicInteger(1);
        List<DemoProfessional> demos = demoProfessionals();
        for (int i = 0; i < demos.size(); i++) {
            DemoProfessional demo = demos.get(i);
            City city = cityNamed(demo.city(), demo.province());
            if (city == null) {
                city = cities.get(i % cities.size());
            }
            Profession profession = professionNamed(demo.profession());
            if (profession == null) {
                profession = professions.get(i % professions.size());
            }
            saveUser(
                    demo.firstName(),
                    demo.lastName(),
                    email(demo.firstName(), demo.lastName()),
                    encoded,
                    Role.PROFESSIONAL,
                    city,
                    profession,
                    demo.phone(),
                    demo.description()
            );
            created.incrementAndGet();
        }
        log.info("Usuarios demo creados: {}. Password: {}", created.get(), seedProperties.getDemoPassword());
    }

    private void saveUser(
            String firstName,
            String lastName,
            String email,
            String encodedPassword,
            Role role,
            City city,
            Profession profession,
            String phone,
            String description
    ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setCountry("Argentina");
        user.setIsActive(true);
        if (city != null) {
            user.setCity(city);
            user.setProvince(city.getProvince());
        }
        user.setProfession(profession);
        user.setPhone(phone);
        user.setDescription(description);
        if (role == Role.PROFESSIONAL) {
            user.setQualification(4);
        }
        user.setImageUrl(downloadAvatar(email));
        userRepository.save(user);
    }

    private int fillMissingAvatars() {
        int filled = 0;
        for (User user : userRepository.findByEmailEndingWith("@lookingprof.dev")) {
            if (user.getImageUrl() != null && !user.getImageUrl().isBlank()) {
                continue;
            }
            String stored = downloadAvatar(user.getEmail());
            if (stored != null) {
                user.setImageUrl(stored);
                userRepository.save(user);
                filled++;
            }
        }
        return filled;
    }

    private String downloadAvatar(String seed) {
        try {
            byte[] bytes = avatarClient.get()
                    .uri("/api/avatar?seed={seed}&size=256", seed)
                    .retrieve()
                    .body(byte[].class);
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return imageService.storeBytes(bytes, "jpg");
        } catch (Exception ex) {
            log.warn("No se pudo descargar el avatar de {}: {}", seed, ex.getMessage());
            return null;
        }
    }

    private static RestClient avatarClient() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(20));
        return RestClient.builder()
                .baseUrl("https://narkis.ai")
                .requestFactory(factory)
                .defaultHeader("User-Agent", "LookingProf/local-demo-avatars")
                .defaultHeader("Accept", "image/jpeg")
                .build();
    }

    private City cityNamed(String cityName, String provinceName) {
        Province province = provincesRepository.findByNameProvince(provinceName);
        if (province == null) {
            return null;
        }
        return cityRepository.findByNameCityIgnoreCase(cityName).stream()
                .filter(city -> city.getProvince() != null
                        && city.getProvince().getIdProvince() == province.getIdProvince())
                .findFirst()
                .orElse(null);
    }

    private Profession professionNamed(String name) {
        return professionRepository.findByNameProfession(name);
    }

    private static String email(String firstName, String lastName) {
        String slug = (firstName + "." + lastName)
                .toLowerCase(Locale.ROOT)
                .replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
                .replaceAll("[^a-z.]", "");
        return slug + "@lookingprof.dev";
    }

    private static List<DemoProfessional> demoProfessionals() {
        return List.of(
                new DemoProfessional("Martín", "Gómez", "Plomero(a)", "Córdoba", "Córdoba",
                        "3515550101", "Plomería particular y consorcios. Urgencias en zona norte."),
                new DemoProfessional("Sofía", "Benítez", "Nutricionista", "Posadas", "Misiones",
                        "3765550102", "Planes alimentarios y seguimiento presencial u online."),
                new DemoProfessional("Pablo", "Ruiz", "Electricista", "Rosario", "Santa Fe",
                        "3415550103", "Instalaciones, tableros y certificación de seguridad eléctrica."),
                new DemoProfessional("Valentina", "López", "Psicólogo(a)", "Palermo", "Ciudad Autónoma de Buenos Aires",
                        "1155550104", "Atención de adultos. Turnos presenciales y virtuales."),
                new DemoProfessional("Diego", "Sosa", "Albañil", "Mendoza", "Mendoza",
                        "2615550105", "Refacciones, revoques y construcciones chicas."),
                new DemoProfessional("Camila", "Pereyra", "Entrenador(a) personal", "La Plata", "Buenos Aires",
                        "2215550106", "Entrenamiento funcional y planes a medida."),
                new DemoProfessional("Jorge", "Acosta", "Gasfitero(a)", "Salta", "Salta",
                        "3875550107", "Gasista matriculado. Cocinas, calefones y cañerías."),
                new DemoProfessional("Florencia", "Ibarra", "Abogado(a)", "San Miguel de Tucumán", "Tucumán",
                        "3815550108", "Laboral y civil. Primera consulta para orientar el caso."),
                new DemoProfessional("Nicolás", "Vega", "Técnico(a) informático", "Neuquén", "Neuquén",
                        "2995550109", "Armado de PCs, redes hogareñas y soporte a pymes."),
                new DemoProfessional("Ana", "Morales", "Jardinero(a)", "San Carlos de Bariloche", "Río Negro",
                        "2944550110", "Mantenimiento de jardines y espacios verdes."),
                new DemoProfessional("Lucas", "Herrera", "Pintor(a)", "Resistencia", "Chaco",
                        "3625550111", "Pintura interior y exterior, trabajos en altura."),
                new DemoProfessional("Mariana", "Quiroga", "Contador(a)", "Paraná", "Entre Ríos",
                        "3435550112", "Monotributo, sueldos y liquidaciones para independientes.")
        );
    }

    private record DemoProfessional(
            String firstName,
            String lastName,
            String profession,
            String city,
            String province,
            String phone,
            String description
    ) {
    }
}
