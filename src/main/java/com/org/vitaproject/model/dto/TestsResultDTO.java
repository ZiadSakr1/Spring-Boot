package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestsResultDTO {
    private String patientName;
    private Long ID;
    private String value;
    private String unites;
    private String category;
    private String code;
    private Boolean is_abnormal;
    private String description;
    private LocalDateTime createdAt;
}
