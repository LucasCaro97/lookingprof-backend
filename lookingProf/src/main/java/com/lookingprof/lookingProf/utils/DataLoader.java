package com.lookingprof.lookingProf.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lookingprof.lookingProf.model.City;
import com.lookingprof.lookingProf.model.Enum.Role;
import com.lookingprof.lookingProf.model.Profession;
import com.lookingprof.lookingProf.model.Province;
import com.lookingprof.lookingProf.model.User;
import com.lookingprof.lookingProf.repository.ICityRepository;
import com.lookingprof.lookingProf.repository.IProfessionRepository;
import com.lookingprof.lookingProf.repository.IProvincesRepository;
import com.lookingprof.lookingProf.repository.IUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader  {

    private final IProvincesRepository iProvincesRepository;
    private final ICityRepository iCityRepository;
    private final IProfessionRepository iProfessionRepository;

    @PostConstruct
    public void loadData(){
        if (iProvincesRepository.count() == 0) {
        List<String> provinces = List.of("Buenos Aires", "Catamarca", "Chaco", "Chubut", "Córdoba", "Corrientes",
                    "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", "Mendoza", "Misiones", "Neuquén", "Río Negro",
                    "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe", "Santiago del Estero", "Tierra del Fuego, Antártida e Islas del Atlántico Sur",
                    "Tucumán"
            );

        List<Province> provinceList = provinces.stream().map(province -> {
                Province province1 = new Province();
                province1.setNameProvince(province);
                return province1;
            }).toList();

            iProvincesRepository.saveAll(provinceList);
    }

        if(iCityRepository.count() == 0){
            List<String> ciudadesMisiones = List.of("Montecarlo","Eldorado", "San Pedro","Cainguás","Libertador General San Martín","Posadas","Candelaria",
                    "General Manuel Belgrano","Guaraní","Iguazú","25 de Mayo","Oberá","San Ignacio","Leandro N. Alem","San Javier","Concepción","Apóstoles");

            List<City> citiesMisiones = ciudadesMisiones.stream().map(ciudad -> {
                City city = new City();
                city.setNameCity(ciudad);
                city.setProvince(iProvincesRepository.findById(13).get());
                return city;
            }).toList();
                iCityRepository.saveAll(citiesMisiones);
        }


                if (iProfessionRepository.count() == 0) {
            List<String> professions = List.of("Ama de llaves", "Albañil", "Carpintero(a)", "Cerrajero(a)", "Cocinera(o)",
                    "Costurera(o)", "Cuidadora de niños", "Cuidadora de personas mayores", "Decorador", "Electricista",
                    "Fontanero(a)", "Fumigador(a)", "Gasfitero(a)", "Jardinero(a)", "Lavaplatos", "Limpiador(a)", "Mecánico",
                    "Mozo(a) de limpieza", "Pintor(a)", "Plomero(a)", "Reparador(a) de electrodomésticos", "Reparador(a) de muebles",
                    "Reparador(a) de tejados", "Reparador(a) de ventanas", "Restaurador(a)", "Sastre", "Tapicero(a)",
                    "Técnico(a) de aire acondicionado", "Técnico(a) de calefacción", "Técnico(a) informático", "Yesero(a)", "Zapatero(a)"
            );

            List<Profession> professionList = professions.stream().map(profession -> {
                Profession profession1 = new Profession();
                profession1.setNameProfession(profession);
                return profession1;
            }).toList();

                    iProfessionRepository.saveAll(professionList);
        }
    }


//    @Autowired
//    IProvincesRepository provincesRepository;
//
//    @Autowired
//    IProfessionRepository professionRepository;
//
//    @Autowired
//    IUserRepository userRepository;
//
//    @Autowired
//    ICityRepository cityRepository;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Autowired
//    UserData userData;
//
//    @Override
//    public void run(String... args) throws Exception {
//        loadData();
//    }
//
//    private void loadData() throws JsonProcessingException {
//
//        if (provincesRepository.count() == 0) {
//            List<String> provinces = List.of("Buenos Aires", "Catamarca", "Chaco", "Chubut", "Córdoba", "Corrientes",
//                    "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", "Mendoza", "Misiones", "Neuquén", "Río Negro",
//                    "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe", "Santiago del Estero", "Tierra del Fuego, Antártida e Islas del Atlántico Sur",
//                    "Tucumán"
//            );
//            List<Province> provinceList = provinces.stream().map(province -> {
//                Province province1 = new Province();
//                province1.setNameProvince(province);
//                return province1;
//            }).toList();
//
//            provincesRepository.saveAll(provinceList);
//        }
//
//        if (professionRepository.count() == 0) {
//            List<String> professions = List.of("Ama de llaves", "Albañil", "Carpintero(a)", "Cerrajero(a)", "Cocinera(o)",
//                    "Costurera(o)", "Cuidadora de niños", "Cuidadora de personas mayores", "Decorador", "Electricista",
//                    "Fontanero(a)", "Fumigador(a)", "Gasfitero(a)", "Jardinero(a)", "Lavaplatos", "Limpiador(a)", "Mecánico",
//                    "Mozo(a) de limpieza", "Pintor(a)", "Plomero(a)", "Reparador(a) de electrodomésticos", "Reparador(a) de muebles",
//                    "Reparador(a) de tejados", "Reparador(a) de ventanas", "Restaurador(a)", "Sastre", "Tapicero(a)",
//                    "Técnico(a) de aire acondicionado", "Técnico(a) de calefacción", "Técnico(a) informático", "Yesero(a)", "Zapatero(a)"
//            );
//
//            List<Profession> professionList = professions.stream().map(profession -> {
//                Profession profession1 = new Profession();
//                profession1.setNameProfession(profession);
//                return profession1;
//            }).toList();
//
//            professionRepository.saveAll(professionList);
//        }
//
//        if (userRepository.count() == 0) {
//            List<Profession> professionList = professionRepository.findAll();
//
//            Optional<Province> province = provincesRepository.findById(12);
//
//            City city = new City();
//            city.setNameCity("Mendoza");
//            city.setProvince(province.get());
//            cityRepository.save(city);
//
//            /**
//             * User 1
//             */
//            User user = new User();
//            user.setFirstName("Miguel");
//            user.setLastName("Rodríguez");
//            user.setEmail("miguelrodriguez@gmail.com");
//            user.setPassword(passwordEncoder.encode("user1"));
//            user.setPhone("+5491112345678");
//            user.setAddress("Calle Falsa 1234");
//            user.setCountry("Argentina");
//            user.setProvince(province.get());
//            user.setCity(city);
//            user.setRole(Role.USER);
//
//            userRepository.save(user);
//
//            /**
//             * User 2
//             */
//            Optional<Province> province2 = provincesRepository.findById(1);
//
//            City city2 = new City();
//            city2.setNameCity("Río Cuarto");
//            city2.setProvince(province2.get());
//            cityRepository.save(city2);
//
//            User user2 = new User();
//            user2.setFirstName("Juan");
//            user2.setLastName("López");
//            user2.setEmail("juanlopez@gmail.com");
//            user2.setPassword(passwordEncoder.encode("user2"));
//            user2.setPhone("+5491252985471");
//            user2.setAddress("Calle Nueva 2345");
//            user2.setCountry("Argentina");
//            user2.setProvince(province2.get());
//            user2.setCity(city2);
//            user2.setRole(Role.USER);
//
//            userRepository.save(user2);
//
//            /**
//             * Professional Users
//             */
//            List<Map<String, Object>> userDataList1 = userData.jsonData();
//            loadUserInDatabase(userDataList1);
//
//            List<Map<String, Object>> userDataList2 = userData.jsonData2();
//            loadUserInDatabase(userDataList2);
//
//            List<Map<String, Object>> userDataList3 = userData.jsonData3();
//            loadUserInDatabase(userDataList3);
//
//            List<Map<String, Object>> userDataList4 = userData.jsonData4();
//            loadUserInDatabase(userDataList4);
//        }
//
//    }
//
//    public void loadUserInDatabase(List<Map<String, Object>> userDataList){
//        List<Province> provinces = provincesRepository.findAll();
//        List<Profession> professions = professionRepository.findAll();
//
//        for (int i = 0; i < userDataList.size(); i++) {
//            City city3 = new City();
//            city3.setNameCity((String) userDataList.get(i).get("city"));
//            city3.setProvince(provinces.get(i));
//            cityRepository.save(city3);
//
//            User user3 = new User();
//            user3.setFirstName((String) userDataList.get(i).get("firstName"));
//            user3.setLastName((String) userDataList.get(i).get("lastName"));
//            user3.setEmail((String) userDataList.get(i).get("email"));
//            user3.setPassword(passwordEncoder.encode((String) userDataList.get(i).get("password")));
//            user3.setPhone((String) userDataList.get(i).get("phone"));
//            user3.setAddress((String) userDataList.get(i).get("address"));
//            user3.setCountry((String) userDataList.get(i).get("country"));
//            user3.setProvince(provinces.get(i));
//            user3.setCity(city3);
//            user3.setQualification((Integer) userDataList.get(i).get("qualification"));
//            user3.setRole(Role.PROFESSIONAL);
//            user3.setProfession(professions.get(i));
//            user3.setDescription((String) userDataList.get(i).get("description"));
//
//            userRepository.save(user3);
//        }
//    }
}
