package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionViewDTO  {
    private Long id;
    private String patientName;
    private String doctorName;
    private List<MedicineDTO> medicines;
    private List<XRayInPrescriptionDTO> xrayes;
    private List<TestsInPrescriptionDTO> tests;
    private String note;
    private String diagnosis;
    private LocalDateTime createdAt;
}
