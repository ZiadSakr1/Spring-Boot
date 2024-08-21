package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepo extends JpaRepository<DoctorEntity,String> {
   Boolean existsByDoctorName(String doctorName);
}
