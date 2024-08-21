package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.DoctorViewToPatientDTO;
import com.org.vitaproject.model.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorViewToPatientDTO toDoctorViewToPatientDTO(UserEntity doctor);

}
