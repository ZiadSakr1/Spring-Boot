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
@Table(name = "doctors_patients")
@AllArgsConstructor
@NoArgsConstructor
public class DoctorPatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_patient_id")
    private Long doctorPatientId;
    @Column(name = "patient_name")
    private String patientName;
    @Column(name = "doctor_name")
    private String doctorName;

    private Integer whichRequestAccess;

    private Boolean access;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "patient_name", insertable = false, updatable = false)
    private PatientEntity patientEntity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "doctor_name", insertable = false, updatable = false)
    private DoctorEntity doctor;

}
