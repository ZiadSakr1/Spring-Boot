package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "xrays_in_prescription")
@AllArgsConstructor
@NoArgsConstructor
public class XRayInPrescriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_id")
    private Long prescriptionId;

    private String xRay;

    private String note;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "prescription_id", insertable = false, updatable = false)
    PrescriptionEntity prescriptionEntity;
}
