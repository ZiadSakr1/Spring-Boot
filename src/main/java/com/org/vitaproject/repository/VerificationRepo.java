package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepo extends JpaRepository<VerificationEntity, Long> {
    VerificationEntity findByToken(String token);

    void deleteByUsername(String username);
}
