package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.model.entity.UserEntity;
import org.mapstruct.Mapper;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserRegisterDTO userRegisterDTO);

    PatientViewDTO toPatientViewDTO(UserEntity userEntity) throws IOException;

    DoctorViewToPatientDTO toDoctorViewToPatientDTO(UserEntity userEntity);
    PatientViewAsListDTO toPatientViewToDoctorAsListDTO(UserEntity userEntity);
    GeneralInfoOfUserDTO toGeneralInfoOfUserDto(UserEntity userEntity);
}
