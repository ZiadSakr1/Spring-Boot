package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.TestsPrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TestsInPrescriptionRepo extends JpaRepository<TestsPrescriptionEntity, Long> {
    List<TestsPrescriptionEntity> findAllByPrescriptionId(Long id);

}
