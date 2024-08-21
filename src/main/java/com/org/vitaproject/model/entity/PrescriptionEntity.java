package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "prescriptions")
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Long id;

    @Column(name = "patient_name")
    private String patientName;
    @Column(name = "doctor_name")
    private String doctorName;

    private String note;
    private String diagnosis;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "patient_name", insertable = false, updatable = false)
    private PatientEntity patientEntity;

    @JsonManagedReference
    @OneToMany(mappedBy = "prescriptionEntity", fetch = FetchType.LAZY)
    private List<MedicineInPrescriptionEntity> medicineEntities;

    @JsonManagedReference
    @OneToMany(mappedBy = "prescriptionEntity", fetch = FetchType.LAZY)
    private List<XRayInPrescriptionEntity> xRayInPrescriptionEntities;

    @JsonManagedReference
    @OneToMany(mappedBy = "prescriptionEntity", fetch = FetchType.LAZY)
    private List<TestsPrescriptionEntity> testsInPrescriptionEntities;

}
