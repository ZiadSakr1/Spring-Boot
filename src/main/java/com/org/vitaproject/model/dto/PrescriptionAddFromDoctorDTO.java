package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionAddFromDoctorDTO {
    private String patientName;
    private List<MedicineDTO> medicines;
    private List<XRayInPrescriptionDTO> xr;
    private List<TestsInPrescriptionDTO> tests;
    private String note;
    private String diagnosis;
}
