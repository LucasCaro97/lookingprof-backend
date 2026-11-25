package com.lookingprof.lookingProf.controller;

import com.lookingprof.lookingProf.dto.SupportContactRequestDTO;
import com.lookingprof.lookingProf.model.SupportContact;
import com.lookingprof.lookingProf.service.SupportContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supportContact")
public class SupportContactController {

    private final SupportContactService supportContactService;

    @GetMapping("/get")
    public List<SupportContact> getAllSupportContact() {
        return supportContactService.getAllSupportContact();
    }

    @GetMapping("/get/{idSupportContact}")
    public SupportContact getSupportContactById(@PathVariable Integer idSupportContact) {
        return supportContactService.findSupportContactById(idSupportContact);
    }

    @PostMapping("/create")
    public String createSupportContact(@RequestBody SupportContact supportContact) {
        supportContactService.saveSupportContact(supportContact);
        return "Create SupportContact";
    }

    @PostMapping("/send")
    public String sendSupportContact(@RequestBody SupportContactRequestDTO request) {
        SupportContact supportContact = new SupportContact();
        supportContact.setUserName(firstNonBlank(request.getFromName(), request.getUserName()));
        supportContact.setLastName(request.getLastName());
        supportContact.setEmail(firstNonBlank(request.getFromEmail(), request.getEmail()));
        supportContact.setAsunt(firstNonBlank(request.getSubject(), request.getAsunt()));
        supportContact.setDescription(firstNonBlank(request.getText(), request.getDescription()));
        supportContactService.saveSupportContact(supportContact);
        return "Create SupportContact";
    }

    @DeleteMapping("/delete/{idSupportContact}")
    public String deleteSupportContact(@PathVariable Integer idSupportContact) {
        supportContactService.deleteSupportContact(idSupportContact);
        return "Delete SupportContact";
    }

    @PutMapping("/edit")
    public SupportContact editSupportContact(@RequestBody SupportContact supportContact) {
        supportContactService.editSupportContact(supportContact);
        return supportContactService.findSupportContactById(supportContact.getIdSuportContact());
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}
