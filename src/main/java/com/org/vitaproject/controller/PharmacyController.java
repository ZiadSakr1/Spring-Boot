package com.org.vitaproject.controller;

import com.org.vitaproject.model.dto.MedicineDTO;
import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import com.org.vitaproject.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Pharmacy")
public class PharmacyController {
    private final PharmacyService pharmacyService;



    @GetMapping("/get-prescription")
    public PrescriptionViewDTO getPrescription(@RequestParam Long ID,
                                               @RequestParam String pharmacistName) {
        return pharmacyService.getPrescription(ID, pharmacistName);
    }

}
