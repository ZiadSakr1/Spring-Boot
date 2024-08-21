package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorViewToPatientDTO {
    private String username;
    private String fullName;
    private String phone;
    private String gender;
    private String specialization;
}
