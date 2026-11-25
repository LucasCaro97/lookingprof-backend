package com.lookingprof.lookingProf.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profession")
    private int idProfession;

    @Column(name = "name_profession", unique = true, length = 120)
    private String nameProfession;

    @OneToMany(mappedBy = "profession")
    private List<User> listUsers;

}
