package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "works_in")
@AllArgsConstructor
@NoArgsConstructor
public class WorksInEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long workId;
    @Column(name = "user_name")
    private String username;
    @Column(name = "organization_name")
    private String organizationName;

    Boolean admin;

    private String type;
    @CreationTimestamp
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_name", insertable = false, updatable = false)
    private UserEntity userEntity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organization_name", insertable = false, updatable = false)
    private OrganizationEntity organizationEntity;


}
