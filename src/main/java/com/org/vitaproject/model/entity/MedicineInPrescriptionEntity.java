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
@Table(name = "medicines_in_prescriptions")
@AllArgsConstructor
@NoArgsConstructor
public class MedicineInPrescriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "med_id")
    private Long id;
    @Column(name = "prescription_id")
    private Long prescriptionId;
    private String medicine;
    private String note;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "prescription_id", insertable = false, updatable = false)
    PrescriptionEntity prescriptionEntity;
}
