package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BigDataProfileDTO {
    private String organizationName;
    private String username;
    private String email;
    private String password;
    private String newPassword;

    public BigDataProfileDTO(String organizationName, String username, String email) {
        this.organizationName = organizationName;
        this.username = username;
        this.email = email;
    }
}
