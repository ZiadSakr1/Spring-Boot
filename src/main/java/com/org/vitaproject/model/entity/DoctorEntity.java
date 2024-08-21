package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "doctors")
@AllArgsConstructor
@NoArgsConstructor

public class DoctorEntity {

    @Id
    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "specialization")
    @NotNull
    private String specialization;

    @JsonManagedReference
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<DoctorPatientEntity> doctorPatientEntities;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "doctor_name", insertable = false, updatable = false)
    private UserEntity userEntity;

}
