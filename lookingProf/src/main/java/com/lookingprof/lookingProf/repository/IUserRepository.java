package com.lookingprof.lookingProf.repository;

import com.lookingprof.lookingProf.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

    List<User> findByFirstName(String userName);

    Optional<User> findByEmail(String email);

    List<User> findByEmailEndingWith(String suffix);

    List<User> findByProfession_NameProfession(String profession);

    List<User> findByProvince_NameProvince(String province);

    List<User> findByCity_NameCity(String city);

    List<User> findByQualification(int qualification);

    @Query("SELECT u FROM User u WHERE u.isActive = true OR u.isActive IS NULL")
    List<User> findAllNotDeleted();

    @Query("""
            SELECT u FROM User u
            WHERE u.role = com.lookingprof.lookingProf.model.Enum.Role.PROFESSIONAL
              AND (u.isActive = true OR u.isActive IS NULL)
              AND (:profession IS NULL OR LOWER(u.profession.nameProfession) = LOWER(:profession))
              AND (:province IS NULL OR LOWER(u.province.nameProvince) = LOWER(:province))
              AND (:city IS NULL OR LOWER(u.city.nameCity) = LOWER(:city))
              AND (:professionId IS NULL OR u.profession.idProfession = :professionId)
              AND (:provinceId IS NULL OR u.province.idProvince = :provinceId)
              AND (:cityId IS NULL OR u.city.idCity = :cityId)
            """)
    Page<User> searchProfessionals(
            @Param("profession") String profession,
            @Param("province") String province,
            @Param("city") String city,
            @Param("professionId") Integer professionId,
            @Param("provinceId") Integer provinceId,
            @Param("cityId") Integer cityId,
            Pageable pageable
    );
}
