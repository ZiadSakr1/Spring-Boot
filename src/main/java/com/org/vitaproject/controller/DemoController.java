package com.org.vitaproject.controller;

import com.org.vitaproject.model.entity.PatientEntity;
import com.org.vitaproject.model.entity.UserEntity;
import com.org.vitaproject.repository.PatientRepo;
import com.org.vitaproject.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class DemoController {
    private final UserRepo userRepo;
    private final PatientRepo patientRepo;
    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok(getUsername());
    }
    @GetMapping("/add")
    public boolean addPatientProfile() {
        Optional<UserEntity> userEntity = userRepo.findByUsername(getUsername());
        UserEntity user = userEntity.get();
        if (user.getPatientEntity() == null) {
            String roles = user.getRole();
            if (!roles.isEmpty()) {
                roles += ", ";
            }
            roles += "PATIENT";
            user.setRole(roles);
            PatientEntity patientEntity = new PatientEntity(userEntity.get().getUsername());
            patientRepo.save(patientEntity);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @GetMapping("/admin_only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Hello from admin only url");
    }
}