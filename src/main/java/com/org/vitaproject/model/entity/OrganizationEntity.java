package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "organizations")
@AllArgsConstructor
@NoArgsConstructor

public class OrganizationEntity {

    @Id
    @Column(name = "organization_name")
    private String organizationName;
    @Email
    private String email;
    private String location;
    private String phone;
    private String type;

    @JsonManagedReference
    @OneToMany(mappedBy = "organization",fetch = FetchType.LAZY)
    private List<OrganizationPatientEntity> organizationPatientEntities;

    @JsonManagedReference
    @OneToMany(mappedBy = "organizationEntity", fetch = FetchType.LAZY)
    private List<WorksInEntity> employees;
}
