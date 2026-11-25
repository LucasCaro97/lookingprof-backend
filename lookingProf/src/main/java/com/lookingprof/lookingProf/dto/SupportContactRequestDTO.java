package com.lookingprof.lookingProf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportContactRequestDTO {

    private String fromName;
    private String fromEmail;
    private String subject;
    private String text;
    private String userName;
    private String lastName;
    private String email;
    private String asunt;
    private String description;
}
