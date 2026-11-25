package com.lookingprof.lookingProf.seed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.seed")
public class SeedProperties {

    /**
     * Carga provincias, localidades (Georef) y oficios al arrancar.
     */
    private boolean catalog = true;

    /**
     * Usuarios de prueba. Solo para perfiles locales (dev/docker).
     */
    private boolean demoUsers = false;

    private String georefBaseUrl = "https://apis.datos.gob.ar/georef/api";

    private String demoPassword = "Dev12345!";
}
