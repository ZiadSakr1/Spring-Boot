package com.org.vitaproject.service.impl;

import com.org.vitaproject.model.dto.BarsDataDTO;
import com.org.vitaproject.model.dto.BigDataProfileDTO;
import com.org.vitaproject.model.dto.StaticDataDTO;
import com.org.vitaproject.model.dto.TestsListDTO;
import com.org.vitaproject.model.entity.UserEntity;
import com.org.vitaproject.repository.InsightsRepo;
import com.org.vitaproject.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BigDataService {
    private final RestTemplate restTemplate;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final InsightsRepo insightsRepo;

    public String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }


    public ResponseEntity<StaticDataDTO> getStaticData() {
        StaticDataDTO staticData = insightsRepo.getStaticData();
        return ResponseEntity.ok().body(staticData);
    }


    public ResponseEntity<TestsListDTO> getTestsList() {
        TestsListDTO testsListDTO = insightsRepo.getTestsList();
        return ResponseEntity.ok().body(testsListDTO);
    }

    public ResponseEntity<Map<String, Object>> getSelectedTestBars(String description) {
        BarsDataDTO barsDataDTO = insightsRepo.getBarsData();
        Map<String, Object> barData = barsDataDTO.getBarsData().get(description);
        return ResponseEntity.ok().body(barData);
    }

    public BigDataProfileDTO getProfileData() {
        UserEntity user = userRepo.findByUsername(getUsername()).get();
        return new BigDataProfileDTO(
                user.getFullName(),
                getUsername(), user.getEmail()
        );
    }

    public ResponseEntity<?> updateProfileData(BigDataProfileDTO bigDataProfileDTO) {
        UserEntity user = userRepo.findByUsername(getUsername()).get();
        if (bigDataProfileDTO.getPassword() != null) {
            if (!passwordEncoder.matches(bigDataProfileDTO.getPassword(), user.getPassword())) {
                return ResponseEntity.status(403).body("Password incorrect!");
            }
            if (bigDataProfileDTO.getNewPassword() == null) {
                return ResponseEntity.status(403).body("You can't make new password empty!");
            }
            user.setPassword(passwordEncoder.encode(bigDataProfileDTO.getNewPassword()));
        }
        if (bigDataProfileDTO.getOrganizationName() != null) {
            user.setFullName(bigDataProfileDTO.getOrganizationName());
        }
        userRepo.save(user);
        return ResponseEntity.status(200).body("Success!");
    }
}
