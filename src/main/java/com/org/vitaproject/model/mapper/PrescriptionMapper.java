package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.PrescriptionAddFromDoctorDTO;
import com.org.vitaproject.model.entity.PrescriptionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {
    PrescriptionEntity toPrescriptionEntity(PrescriptionAddFromDoctorDTO prescriptionAddFromDoctorDTO);

}
