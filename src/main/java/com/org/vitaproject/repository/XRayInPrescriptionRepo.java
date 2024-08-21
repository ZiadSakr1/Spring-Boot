package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.XRayInPrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface XRayInPrescriptionRepo extends JpaRepository<XRayInPrescriptionEntity, Long> {
    List<XRayInPrescriptionEntity> findAllByPrescriptionId(Long id);
}
