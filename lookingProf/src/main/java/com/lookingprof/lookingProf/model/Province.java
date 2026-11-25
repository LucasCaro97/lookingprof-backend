package com.lookingprof.lookingProf.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_province")
    private int idProvince;

    @Column(name = "name_province", unique = true)
    private String nameProvince;

    @OneToMany(mappedBy = "province")
    List<User> users;


}
