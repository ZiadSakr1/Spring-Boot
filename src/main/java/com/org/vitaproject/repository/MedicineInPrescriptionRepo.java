package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.MedicineInPrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MedicineInPrescriptionRepo extends JpaRepository<MedicineInPrescriptionEntity, Long> {
    List<MedicineInPrescriptionEntity> findAllByPrescriptionId(Long id);
}
