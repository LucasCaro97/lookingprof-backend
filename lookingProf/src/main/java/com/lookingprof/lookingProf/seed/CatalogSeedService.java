package com.lookingprof.lookingProf.seed;

import com.lookingprof.lookingProf.model.City;
import com.lookingprof.lookingProf.model.Profession;
import com.lookingprof.lookingProf.model.Province;
import com.lookingprof.lookingProf.repository.ICityRepository;
import com.lookingprof.lookingProf.repository.IProfessionRepository;
import com.lookingprof.lookingProf.repository.IProvincesRepository;
import com.lookingprof.lookingProf.seed.GeorefClient.GeorefLocalidad;
import com.lookingprof.lookingProf.seed.GeorefClient.GeorefNamed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogSeedService {

    private static final Logger log = LoggerFactory.getLogger(CatalogSeedService.class);

    private final IProvincesRepository provincesRepository;
    private final ICityRepository cityRepository;
    private final IProfessionRepository professionRepository;

    @Transactional
    public void upsertProfessions(List<String> names) {
        Set<String> existing = professionRepository.findAll().stream()
                .map(profession -> normalize(profession.getNameProfession()))
                .collect(Collectors.toSet());
        List<Profession> toInsert = new ArrayList<>();
        for (String name : names) {
            if (existing.add(normalize(name))) {
                Profession profession = new Profession();
                profession.setNameProfession(name);
                toInsert.add(profession);
            }
        }
        if (!toInsert.isEmpty()) {
            professionRepository.saveAll(toInsert);
        }
        log.info("Oficios en catálogo: {} (altas {})", professionRepository.count(), toInsert.size());
    }

    @Transactional
    public void upsertGeography(List<GeorefNamed> remoteProvinces, List<GeorefLocalidad> localities) {
        Map<String, Province> provincesByName = provincesRepository.findAll().stream()
                .collect(Collectors.toMap(
                        province -> normalize(province.getNameProvince()),
                        Function.identity(),
                        (left, right) -> left
                ));

        int newProvinces = 0;
        for (GeorefNamed remote : remoteProvinces) {
            if (remote.nombre() == null || remote.nombre().isBlank()) {
                continue;
            }
            String key = normalize(remote.nombre());
            if (!provincesByName.containsKey(key)) {
                Province province = new Province();
                province.setNameProvince(remote.nombre().trim());
                provincesByName.put(key, provincesRepository.save(province));
                newProvinces++;
            }
        }

        Set<String> existingCities = new HashSet<>();
        for (City city : cityRepository.findAll()) {
            if (city.getProvince() != null) {
                existingCities.add(cityKey(city.getProvince().getNameProvince(), city.getNameCity()));
            }
        }

        List<City> toInsert = new ArrayList<>();
        for (GeorefLocalidad locality : localities) {
            if (locality.nombre() == null || locality.nombre().isBlank()
                    || locality.provincia() == null || locality.provincia().nombre() == null) {
                continue;
            }
            Province province = provincesByName.get(normalize(locality.provincia().nombre()));
            if (province == null) {
                continue;
            }
            String key = cityKey(province.getNameProvince(), locality.nombre());
            if (!existingCities.add(key)) {
                continue;
            }
            City city = new City();
            city.setNameCity(locality.nombre().trim());
            city.setProvince(province);
            toInsert.add(city);
        }

        if (!toInsert.isEmpty()) {
            cityRepository.saveAll(toInsert);
        }
        log.info("Georef: provincias {} (altas {}), localidades {} (altas {})",
                provincesRepository.count(), newProvinces, cityRepository.count(), toInsert.size());
    }

    private static String cityKey(String province, String city) {
        return normalize(province) + "|" + normalize(city);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
