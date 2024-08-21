package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralInfoOfUserDTO {
    private String username;
    private String fullName;
    private String SSN;
    private String phone;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private String martalStatus;
}
