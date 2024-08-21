package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.OrganizationPatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationPatientRepo extends JpaRepository<OrganizationPatientEntity, Long> {

    Optional<OrganizationPatientEntity> findByOrganizationNameAndPatientName(String patientName
            , String xRayLaboratoryName);

    List<OrganizationPatientEntity> findAllByOrganizationName(String OrganizationName);

    List<OrganizationPatientEntity> findAllByPatientName(String patientName);
}
