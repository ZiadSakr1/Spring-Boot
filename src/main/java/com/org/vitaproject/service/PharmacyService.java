package com.org.vitaproject.service;

import com.org.vitaproject.model.dto.PrescriptionViewDTO;

public interface PharmacyService {
    int canAccessThisLab(String pharmacistName);

    PrescriptionViewDTO getPrescription(Long id, String pharmacistName);

}
