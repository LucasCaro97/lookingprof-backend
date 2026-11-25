package com.lookingprof.lookingProf.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed.catalog", havingValue = "true", matchIfMissing = true)
public class GeorefClient {

    private static final Logger log = LoggerFactory.getLogger(GeorefClient.class);

    private final RestClient restClient;

    public GeorefClient(SeedProperties properties) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(30));
        this.restClient = RestClient.builder()
                .baseUrl(properties.getGeorefBaseUrl())
                .requestFactory(factory)
                .defaultHeader("User-Agent", "LookingProf/local-catalog-seed")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public List<GeorefNamed> fetchProvinces() {
        ProvinciasResponse response = restClient.get()
                .uri("/provincias?campos=id,nombre&max=24&orden=nombre")
                .retrieve()
                .body(ProvinciasResponse.class);
        if (response == null || response.provincias() == null) {
            throw new IllegalStateException("Georef no devolvió provincias");
        }
        return response.provincias();
    }

    public List<GeorefLocalidad> fetchLocalities() {
        try {
            return fetchLocalities("/localidades?campos=nombre,provincia.nombre&max=5000&orden=nombre");
        } catch (RestClientException ex) {
            log.warn("No se pudieron bajar todas las localidades de una vez, se intenta por provincia: {}", ex.getMessage());
            return fetchLocalitiesByProvince();
        }
    }

    private List<GeorefLocalidad> fetchLocalitiesByProvince() {
        return fetchProvinces().stream()
                .flatMap(province -> fetchLocalities(
                        "/localidades?provincia=" + province.id() + "&campos=nombre,provincia.nombre&max=5000&orden=nombre"
                ).stream())
                .toList();
    }

    private List<GeorefLocalidad> fetchLocalities(String uri) {
        LocalidadesResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(LocalidadesResponse.class);
        if (response == null || response.localidades() == null) {
            throw new IllegalStateException("Georef no devolvió localidades");
        }
        log.info("Georef localidades: {} (total declarado {})", response.localidades().size(), response.total());
        return response.localidades();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProvinciasResponse(List<GeorefNamed> provincias) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LocalidadesResponse(List<GeorefLocalidad> localidades, Integer total) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeorefNamed(String id, String nombre) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeorefLocalidad(String nombre, GeorefNamed provincia) {
    }
}
