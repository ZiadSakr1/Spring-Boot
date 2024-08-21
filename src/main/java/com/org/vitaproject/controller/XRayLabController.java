package com.org.vitaproject.controller;

import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import com.org.vitaproject.model.dto.XRayInPrescriptionDTO;
import com.org.vitaproject.service.XRayLaboratoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/XRay-Lab")
public class XRayLabController {
    private final XRayLaboratoryService xRayLaboratoryService;


    @GetMapping("/get-prescription") 
    public PrescriptionViewDTO getPrescription(@RequestParam Long ID,
                                               @RequestParam String xRayLaboratoryName) {
        return xRayLaboratoryService.getPrescription(ID, xRayLaboratoryName);
    }

    @PostMapping("/add-xRay-result")
    public String addXRayResult(@RequestParam String xRayLaboratoryName, @RequestParam String patientName
            , @RequestParam String category, @RequestParam("image") MultipartFile file) throws IOException {
        return xRayLaboratoryService.addXRayResult(xRayLaboratoryName
                , patientName, category, file);
    }

}
