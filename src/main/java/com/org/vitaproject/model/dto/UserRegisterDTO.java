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

public class UserRegisterDTO {
    private String username;
    private String SSN;
    private String fullName;
    private String password;
    private String phone;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String martalStatus;
}
