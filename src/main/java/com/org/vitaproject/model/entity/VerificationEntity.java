package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "verification")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @Column(name = "user_name")
    private String username;
    @JsonBackReference
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_name", insertable = false, updatable = false)
    private UserEntity userEntity;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;


    public VerificationEntity(String token, UserEntity user, int time) {
        this.token = token;
        this.userEntity = user;
        this.expiryDate = calculateExpiryDate(time); // 60 minutes expiry time
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.getTime();
    }
}
