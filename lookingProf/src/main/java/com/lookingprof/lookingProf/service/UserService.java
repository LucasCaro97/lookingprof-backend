package com.lookingprof.lookingProf.service;

import com.lookingprof.lookingProf.config.Auth.AuthResponse;
import com.lookingprof.lookingProf.config.Auth.LoginRequest;
import com.lookingprof.lookingProf.config.Auth.RegisterRequest;
import com.lookingprof.lookingProf.dto.ContactResponseDTO;
import com.lookingprof.lookingProf.dto.PageResponse;
import com.lookingprof.lookingProf.dto.ProfessionalPublicDTO;
import com.lookingprof.lookingProf.dto.UserRequestDTO;
import com.lookingprof.lookingProf.dto.UserResponseDTO;
import com.lookingprof.lookingProf.exceptions.UserDeleteException;
import com.lookingprof.lookingProf.exceptions.UserNotFoundException;
import com.lookingprof.lookingProf.exceptions.UserUpdateException;
import com.lookingprof.lookingProf.jwt.JwtService;
import com.lookingprof.lookingProf.model.City;
import com.lookingprof.lookingProf.model.Enum.Role;
import com.lookingprof.lookingProf.model.Province;
import com.lookingprof.lookingProf.model.User;
import com.lookingprof.lookingProf.repository.ICityRepository;
import com.lookingprof.lookingProf.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    CityService cityService;

    @Autowired
    ICityRepository cityRepository;

    @Autowired
    ProvinceService provinceService;
    @Autowired
    ProfessionService professionService;
    @Autowired
    ImageService imageService;

    @Override
    @Transactional(readOnly = true)
    public Optional<List<UserResponseDTO>>  listAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return Optional.empty();
        }
        List<UserResponseDTO> listUserDTO = new ArrayList<>();
        users.forEach(user -> {
            UserResponseDTO userResponseDTO = new UserResponseDTO(user);
            listUserDTO.add(userResponseDTO);
        });
        return Optional.of(listUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalPublicDTO> listPublicProfessionals() {
        return userRepository.findAllNotDeleted().stream()
                .filter(user -> user.getRole() == Role.PROFESSIONAL)
                .map(ProfessionalPublicDTO::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProfessionalPublicDTO> searchProfessionals(
            String profession,
            String province,
            String city,
            Integer professionId,
            Integer provinceId,
            Integer cityId,
            Pageable pageable
    ) {
        Page<ProfessionalPublicDTO> page = userRepository.searchProfessionals(
                blankToNull(profession),
                blankToNull(province),
                blankToNull(city),
                professionId,
                provinceId,
                cityId,
                pageable
        ).map(ProfessionalPublicDTO::new);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfessionalPublicDTO> findPublicById(Integer id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole() == Role.PROFESSIONAL)
                .filter(user -> user.getIsActive() == null || user.getIsActive())
                .map(ProfessionalPublicDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponseDTO getContact(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontraron usuarios"));
        if (user.getRole() != Role.PROFESSIONAL) {
            throw new UserNotFoundException("No se encontraron usuarios");
        }
        return new ContactResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<UserResponseDTO>> findByFirstname(String firstName) {
        List<User> users = userRepository.findByFirstName(firstName);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        List<UserResponseDTO> listUserDTO = new ArrayList<>();
        users.forEach(user -> {
            UserResponseDTO userResponseDTO = new UserResponseDTO(user);
            listUserDTO.add(userResponseDTO);
        });
        return Optional.of(listUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserResponseDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();
        return Optional.of(new UserResponseDTO(user));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteUser(Integer id) {
        Optional<User> user = userRepository.findById(id);

        User userDelete = user.get();
        try {
            userDelete.setIsActive(false);
            userRepository.save(userDelete);
            return "Usuario eliminado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserDeleteException("Error al eliminar el usuario con ID: " + id, e);
        }
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Optional<UserResponseDTO> updateUser(Integer id, UserRequestDTO userUpdate) {
//        Optional<User> userOptional = userRepository.findById(id);
//        Province province = provinceService.getProvinceById((userUpdate.getProvince()));
//
//        if (userOptional.isEmpty()){
//            return Optional.empty();
//        }
//        User user = userOptional.get();
//        try {
//            user.setProvince(provinceService.getProvinceById(Integer.parseInt(userUpdate.getProvince())));
//            user.setCity(cityService.getCityById(Integer.parseInt(userUpdate.getCity())));
//            user.setProfession(professionService.getById(Integer.parseInt(userUpdate.getProfession())));
//            user.setDescription(userUpdate.getDescription());
//            if(userUpdate.getImage() != null) user.setImageUrl(imageService.copyProfileImg(user.getImageUrl(), userUpdate.getImage()));
//            userRepository.save(user);
//            return Optional.of(new UserResponseDTO(user));
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new UserUpdateException("Error al actualizar el usuario");
//        }
//
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<UserResponseDTO> updateUser(Integer id, UserRequestDTO userUpdate) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()){
            return Optional.empty();
        }
        User user = userOptional.get();
        try {
            if (userUpdate.getProvince() != null) user.setProvince(provinceService.getProvinceById(userUpdate.getProvince()));
            if (userUpdate.getCity() != null) user.setCity(cityService.getCityById(userUpdate.getCity()));
            if (userUpdate.getProfession() != null) user.setProfession(professionService.getById(userUpdate.getProfession()));
            if (userUpdate.getDescription() != null) user.setDescription(userUpdate.getDescription());
            if (userUpdate.getPhone() != null) user.setPhone(userUpdate.getPhone());
            if(userUpdate.getImage() != null) user.setImageUrl(imageService.copyProfileImg(user.getImageUrl(), userUpdate.getImage()));
            userRepository.save(user);
            return Optional.of(new UserResponseDTO(user));
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserUpdateException("Error al actualizar el usuario");
        }
    }

    @Override
    public Optional<List<UserResponseDTO>> findByProfession(String profession) {
        List<User> users = userRepository.findByProfession_NameProfession(profession);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        List<UserResponseDTO> listUserDTO = new ArrayList<>();
        users.forEach(user -> {
            UserResponseDTO userResponseDTO = new UserResponseDTO(user);
            listUserDTO.add(userResponseDTO);
        });
        return Optional.of(listUserDTO);
    }


    @Override
    public Optional<List<UserResponseDTO>> findByProvince(String province) {
        List<User> users = userRepository.findByProvince_NameProvince(province);
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            List<UserResponseDTO> listUserDTO = new ArrayList<>();
            users.forEach(user -> {
                UserResponseDTO userResponseDTO = new UserResponseDTO(user);
                listUserDTO.add(userResponseDTO);
            });
            return Optional.of(listUserDTO);
        }
    }

    @Override
    public Optional<List<UserResponseDTO>> findByCity(String city) {
            List<User> users = userRepository.findByCity_NameCity(city);
            if (users.isEmpty()) {
                return Optional.empty();
            } else {
                List<UserResponseDTO> listUserDTO = new ArrayList<>();
                users.forEach(user -> {
                    UserResponseDTO userResponseDTO = new UserResponseDTO(user);
                    listUserDTO.add(userResponseDTO);
                });
                return Optional.of(listUserDTO);
            }
        }

    @Override
    public Optional<List<UserResponseDTO>> findByQualification(int qualification) {
        List<User> users = userRepository.findByQualification(qualification);
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            List<UserResponseDTO> listUserDTO = new ArrayList<>();
            users.forEach(user -> {
                UserResponseDTO userResponseDTO = new UserResponseDTO(user);
                listUserDTO.add(userResponseDTO);
            });
            return Optional.of(listUserDTO);
        }
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.getToken(user);
        return AuthResponse.builder().token(token).build();
    }

    @Override
    public AuthResponse registerUser(RegisterRequest request) {
        Role requestedRole = request.getRole();
        if (requestedRole == null || requestedRole == Role.ADMIN) {
            throw new IllegalArgumentException("Rol inválido");
        }
        if (requestedRole != Role.USER && requestedRole != Role.PROFESSIONAL) {
            throw new IllegalArgumentException("Rol inválido");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo ingresado ya está registrado");
        }

        Province province = provinceService.getProvinceById(request.getProvinceId());
        City city = cityService.getCityById(request.getCityId());
        if (province == null || city == null) {
            throw new IllegalArgumentException("Provincia o ciudad inválida");
        }
        if (city.getProvince() == null || city.getProvince().getIdProvince() != province.getIdProvince()) {
            throw new IllegalArgumentException("La ciudad no pertenece a la provincia seleccionada");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(requestedRole);
        user.setProvince(province);
        user.setCity(city);
        user.setIsActive(true);
        userRepository.save(user);
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user does not exists"));

        return new org.springframework.security.core.userdetails.User(
                userDetails.getUsername(),
                "",
                userDetails.getAuthorities()
        );
    }

    @Override
    public List<UserResponseDTO> listAllActives() {
        List<User> userList = userRepository.findAllNotDeleted();
        if(!userList.isEmpty()){
            List<UserResponseDTO> listUsersDto = new ArrayList<>();
            userList.forEach(user -> {
                UserResponseDTO userResponseDTO = new UserResponseDTO(user);
                listUsersDto.add(userResponseDTO);
            });
            return listUsersDto;
        }else{
            throw new RuntimeException("No hay usuarios activos registrados");
        }
    }
}
