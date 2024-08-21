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
@Table(name = "tests_in_prescription")
@AllArgsConstructor
@NoArgsConstructor
public class TestsPrescriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tests_id")
    private Long id;

    @Column(name = "prescription_id")
    private Long prescriptionId;

    private String test;
    private String note;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "prescription_id", insertable = false, updatable = false)
    PrescriptionEntity prescriptionEntity;
}
