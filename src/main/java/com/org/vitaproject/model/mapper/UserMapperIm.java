package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.GeneralInfoOfUserDTO;
import com.org.vitaproject.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
@Component
@RequiredArgsConstructor
public class UserMapperIm {
    public GeneralInfoOfUserDTO toGeneralInfoOfUserDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        GeneralInfoOfUserDTO.GeneralInfoOfUserDTOBuilder generalInfoOfUserDTO = GeneralInfoOfUserDTO.builder();
        generalInfoOfUserDTO.username(userEntity.getUsername());
        generalInfoOfUserDTO.SSN(userEntity.getSSN());
        generalInfoOfUserDTO.fullName(userEntity.getFullName());
        generalInfoOfUserDTO.phone(userEntity.getPhone());
        generalInfoOfUserDTO.email(userEntity.getEmail());
        generalInfoOfUserDTO.gender(userEntity.getGender());
        generalInfoOfUserDTO.dateOfBirth(userEntity.getDateOfBirth());
        generalInfoOfUserDTO.address(userEntity.getAddress());
        generalInfoOfUserDTO.martalStatus(userEntity.getMartalStatus());
        generalInfoOfUserDTO.age(Period.between(userEntity.getDateOfBirth(), LocalDate.now()).getYears());
        return generalInfoOfUserDTO.build();
    }
}
