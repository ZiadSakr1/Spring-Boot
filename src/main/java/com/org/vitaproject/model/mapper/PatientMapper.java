package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.PatientViewDTO;
import com.org.vitaproject.model.entity.PatientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientViewDTO toPatientViewToDoctorDTO(PatientEntity patientEntity);


}
