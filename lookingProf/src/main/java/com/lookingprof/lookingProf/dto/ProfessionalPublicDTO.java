package com.lookingprof.lookingProf.dto;

import com.lookingprof.lookingProf.model.Enum.Role;
import com.lookingprof.lookingProf.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalPublicDTO {

    private int idUser;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private ProfessionResponseDTO profession;
    private ProvinceResponseDTO province;
    private CityDTO city;
    private String description;
    private Integer qualification;
    private Role role;

    public ProfessionalPublicDTO(User user) {
        this.idUser = user.getIdUser();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.imageUrl = user.getImageUrl();
        this.profession = user.getProfession() != null ? new ProfessionResponseDTO(user.getProfession()) : null;
        this.province = user.getProvince() != null ? new ProvinceResponseDTO(user.getProvince()) : null;
        this.city = user.getCity() != null ? new CityDTO(user.getCity()) : null;
        this.description = user.getDescription();
        this.qualification = user.getQualification();
        this.role = user.getRole();
    }
}
