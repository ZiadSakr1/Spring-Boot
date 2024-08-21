package com.org.vitaproject.service;

import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface XRayLaboratoryService {

    int canAccessThisLab(String xRayLaboratoryName);


    PrescriptionViewDTO getPrescription(Long id, String xRayLaboratoryName);

    String addXRayResult(String xRayLaboratoryName, String patientName
            , String category, MultipartFile file) throws IOException;
}
