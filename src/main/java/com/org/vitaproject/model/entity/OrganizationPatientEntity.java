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
@Table(name = "organization_patient")
@AllArgsConstructor
@NoArgsConstructor

public class OrganizationPatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long ID;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "organization_name")
    private String organizationName;

    private Boolean access;
    private String type;

    private Boolean whichRequestAccess;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "patient_name", insertable = false, updatable = false)
    private PatientEntity patientEntity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organization_name", insertable = false, updatable = false)
    private OrganizationEntity organization;
}
