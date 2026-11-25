package com.lookingprof.lookingProf.service;

import com.lookingprof.lookingProf.config.Auth.AuthResponse;
import com.lookingprof.lookingProf.config.Auth.LoginRequest;
import com.lookingprof.lookingProf.config.Auth.RegisterRequest;
import com.lookingprof.lookingProf.dto.ContactResponseDTO;
import com.lookingprof.lookingProf.dto.PageResponse;
import com.lookingprof.lookingProf.dto.ProfessionalPublicDTO;
import com.lookingprof.lookingProf.dto.UserRequestDTO;
import com.lookingprof.lookingProf.dto.UserResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface IUserService extends UserDetailsService {

    Optional<List<UserResponseDTO>> listAll();
    List<ProfessionalPublicDTO> listPublicProfessionals();
    PageResponse<ProfessionalPublicDTO> searchProfessionals(
            String profession,
            String province,
            String city,
            Integer professionId,
            Integer provinceId,
            Integer cityId,
            Pageable pageable
    );
    Optional<UserResponseDTO> findById(Integer id);
    Optional<ProfessionalPublicDTO> findPublicById(Integer id);
    ContactResponseDTO getContact(Integer id);
    String deleteUser(Integer id);
    Optional<UserResponseDTO> updateUser(Integer id, UserRequestDTO userDTO);
    Optional<List<UserResponseDTO>> findByFirstname(String firstName);
    Optional<UserResponseDTO> findByEmail(String email);

    Optional<List<UserResponseDTO>> findByProfession(String profession);
    Optional<List<UserResponseDTO>> findByProvince(String province);
    Optional<List<UserResponseDTO>> findByCity(String city);
    Optional<List<UserResponseDTO>> findByQualification(int qualification);

    AuthResponse loginUser(LoginRequest user);
    AuthResponse registerUser(RegisterRequest user);
    UserDetails loadUserByUsername(String username);
    List<UserResponseDTO> listAllActives();
}
