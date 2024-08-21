package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationProfileDTO {
    private String organizationName;
    private String email;
    private String location;
    private String phone;
}
