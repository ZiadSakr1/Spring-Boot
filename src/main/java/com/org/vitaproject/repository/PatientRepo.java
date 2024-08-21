package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepo extends JpaRepository<PatientEntity,String> {

}
