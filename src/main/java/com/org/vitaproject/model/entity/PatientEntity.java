package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "patients")
@AllArgsConstructor
@NoArgsConstructor
public class PatientEntity {
    public PatientEntity(String patientName) {
        this.patientName = patientName;
    }

    @Id
    @Column(name = "patient_name")
    private String patientName;

    @JsonManagedReference
    @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
    private List<XRayEntity> xRayEntities;

    @JsonManagedReference
    @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
    private List<TestsEntity> testsEntities;

    @JsonManagedReference
    @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
    private List<PrescriptionEntity> prescriptionEntities;

    @JsonManagedReference
    @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
    private List<DoctorPatientEntity> doctorPatientEntities;

    @JsonManagedReference
    @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
    private List<OrganizationPatientEntity> organizationPatientEntities;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "patient_name", insertable = false, updatable = false)
    private UserEntity userEntity;
}
