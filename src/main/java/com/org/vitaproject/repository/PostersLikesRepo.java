package com.org.vitaproject.repository;

import com.org.vitaproject.model.entity.PostersEntity;
import com.org.vitaproject.model.entity.PostersLikesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostersLikesRepo extends JpaRepository<PostersLikesEntity, String> {
    boolean existsByIDAndUsername(Long ID, String username);


    @Query("SELECT p.LikeID FROM PostersLikesEntity p where p.username = :username and p.ID = :ID")
    String findByIDAndUsername(Long ID, String username);
}
