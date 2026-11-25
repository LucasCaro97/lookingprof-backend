package com.lookingprof.lookingProf.dto;

import com.lookingprof.lookingProf.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponseDTO {

    private int idUser;
    private String email;
    private String phone;

    public ContactResponseDTO(User user) {
        this.idUser = user.getIdUser();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}
