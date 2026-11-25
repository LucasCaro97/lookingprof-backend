package com.lookingprof.lookingProf.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_city_province_name", columnNames = {"fk_province", "name_city"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_city")
    private int idCity;

    @Column(name = "name_city", length = 180)
    private String nameCity;

    @ManyToOne
    @JoinColumn(name="fk_province")
    private Province province;

    @OneToMany(mappedBy = "city")
    List<User> users;

}
