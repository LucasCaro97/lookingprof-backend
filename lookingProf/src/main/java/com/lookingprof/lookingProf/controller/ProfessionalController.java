package com.lookingprof.lookingProf.controller;

import com.lookingprof.lookingProf.dto.PageResponse;
import com.lookingprof.lookingProf.dto.ProfessionalPublicDTO;
import com.lookingprof.lookingProf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<ProfessionalPublicDTO>> search(
            @RequestParam(required = false) String profession,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer professionId,
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "qualification"));
        return ResponseEntity.ok(userService.searchProfessionals(
                profession, province, city, professionId, provinceId, cityId, pageable
        ));
    }
}
