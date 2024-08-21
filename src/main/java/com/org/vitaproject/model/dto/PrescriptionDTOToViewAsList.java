package com.org.vitaproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDTOToViewAsList {

    private Long prescription_id;
    private LocalDateTime created_at;
    private String doctorName;
}
