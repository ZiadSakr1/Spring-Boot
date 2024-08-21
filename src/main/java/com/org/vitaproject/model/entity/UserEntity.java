package com.org.vitaproject.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.org.vitaproject.Exceptions.MessageError;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
//@Where(clause = "deleted_at is null")
public class UserEntity implements UserDetails {

    @Id
    @Column(name = "user_name")
    private String username;
    @Column(name = "ssn")
    private String SSN;
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "password")
    private String password;
    @Column(name = "phone")
    private String phone;
    private Boolean verified;
    @Email
    @Column(name = "email")
    private String email;
    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "martal_status")
    private String martalStatus;

    @Column(name = "role")
    private String role;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;


    @JsonManagedReference
    @OneToOne(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private PatientEntity patientEntity;

    @JsonManagedReference
    @OneToOne(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private DoctorEntity doctor;

    @JsonManagedReference
    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<WorksInEntity> organization;
    @JsonManagedReference
    @OneToOne(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private VerificationEntity verificationEntity;

    @JsonManagedReference
    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<PostersLikesEntity> postersLikesEntities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role[] = getRole().split(",");
        List<GrantedAuthority> roles = new ArrayList<>();
        if (!getRole().equals("")) {
            for (String userRole : role) {
                roles.add(new SimpleGrantedAuthority(userRole));
            }
        }
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (verified == null || !verified) {
            throw new MessageError("Your Account Needed To be Verified!");
        }
        return true;
    }
}
