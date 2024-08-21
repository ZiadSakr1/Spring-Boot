package com.org.vitaproject.service;

import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import com.org.vitaproject.model.dto.TestsResultDTO;

public interface TestsLabService {
    int canAccessThisLab(String organizationName);

    PrescriptionViewDTO getPrescription(Long id, String laboratoryName);

    String addTestResult(TestsResultDTO testsResultDTO, String laboratoryName);
}
