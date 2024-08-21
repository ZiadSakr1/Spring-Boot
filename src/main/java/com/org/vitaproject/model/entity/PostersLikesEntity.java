package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posters_likes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostersLikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String LikeID;

    @NotNull
    @Column(name = "user_name")
    private String username;

    @NotNull
    private Long ID;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_name", insertable = false, updatable = false)
    private UserEntity userEntity;

}
