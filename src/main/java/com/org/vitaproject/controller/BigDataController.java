package com.org.vitaproject.controller;

import com.org.vitaproject.model.dto.BigDataProfileDTO;
import com.org.vitaproject.model.dto.StaticDataDTO;
import com.org.vitaproject.model.dto.TestsListDTO;
import com.org.vitaproject.service.impl.BigDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class BigDataController {

    private final BigDataService bigDataService;

    @GetMapping("/static-data")
    public ResponseEntity<StaticDataDTO> getStaticData() {
        return bigDataService.getStaticData();
    }

    @GetMapping("/tests-list")
    public ResponseEntity<TestsListDTO> getTestsList() {
        return bigDataService.getTestsList();
    }
    @GetMapping("/selected-test-bars")
    public ResponseEntity<Map<String, Object>> getSelectedTestBars(@RequestParam String description) {
        return bigDataService.getSelectedTestBars(description);
    }

    @GetMapping("/get-profile-data")
    public BigDataProfileDTO getProfileData() {
        return bigDataService.getProfileData();
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfileData(@RequestBody BigDataProfileDTO bigDataProfileDTO) {
        return bigDataService.updateProfileData(bigDataProfileDTO);
    }
}
