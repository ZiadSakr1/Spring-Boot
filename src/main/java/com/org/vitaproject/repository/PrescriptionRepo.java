package com.org.vitaproject.repository;

import com.org.vitaproject.model.dto.PrescriptionDTOToViewAsList;
import com.org.vitaproject.model.entity.PrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepo extends JpaRepository<PrescriptionEntity, Long> {
    List<PrescriptionEntity> findAllByPatientName(String patientName);

    Optional<PrescriptionEntity> findByPatientNameAndId(String patientName, Long id);
    Boolean existsByPatientNameAndId(String patientName, Long id);
    void deleteByPatientNameAndId(String patientName, Long id);
    @Query("SELECT p.id, p.createdAt FROM PrescriptionEntity p WHERE p.patientName = :patientName")
    List<PrescriptionDTOToViewAsList> findAllByPatientName2(String patientName);
}
