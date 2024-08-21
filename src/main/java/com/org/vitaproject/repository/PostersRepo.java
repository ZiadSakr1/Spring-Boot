package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.PostersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostersRepo extends JpaRepository<PostersEntity, Long> {
    @Query("SELECT p FROM PostersEntity p WHERE  p.deleted_at > CURRENT_TIMESTAMP")
    List<PostersEntity> findAllActivePosters(Pageable pageable);
}
