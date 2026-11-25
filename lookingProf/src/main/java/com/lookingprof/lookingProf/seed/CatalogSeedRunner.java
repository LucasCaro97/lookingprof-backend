package com.lookingprof.lookingProf.seed;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.catalog", havingValue = "true", matchIfMissing = true)
public class CatalogSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogSeedRunner.class);

    static final List<String> PROFESSIONS = List.of(
            "Ama de llaves", "Albañil", "Carpintero(a)", "Cerrajero(a)", "Cocinera(o)",
            "Costurera(o)", "Cuidadora de niños", "Cuidadora de personas mayores", "Decorador", "Electricista",
            "Fontanero(a)", "Fumigador(a)", "Gasfitero(a)", "Jardinero(a)", "Lavaplatos", "Limpiador(a)", "Mecánico",
            "Mozo(a) de limpieza", "Pintor(a)", "Plomero(a)", "Reparador(a) de electrodomésticos", "Reparador(a) de muebles",
            "Reparador(a) de tejados", "Reparador(a) de ventanas", "Restaurador(a)", "Sastre", "Tapicero(a)",
            "Técnico(a) de aire acondicionado", "Técnico(a) de calefacción", "Técnico(a) informático", "Yesero(a)", "Zapatero(a)",
            "Nutricionista", "Psicólogo(a)", "Entrenador(a) personal", "Abogado(a)", "Contador(a)", "Médico(a)"
    );

    private final GeorefClient georefClient;
    private final CatalogSeedService catalogSeedService;

    @Override
    public void run(ApplicationArguments args) {
        catalogSeedService.upsertProfessions(PROFESSIONS);
        try {
            catalogSeedService.upsertGeography(georefClient.fetchProvinces(), georefClient.fetchLocalities());
        } catch (Exception ex) {
            log.warn("No se pudo completar el catálogo geográfico desde Georef: {}", ex.getMessage());
        }
    }
}
