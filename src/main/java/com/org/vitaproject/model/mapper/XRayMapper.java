package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.ResponseAsListDTO;
import com.org.vitaproject.model.entity.XRayEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface XRayMapper {
    ResponseAsListDTO toXRayAsListDto(XRayEntity xRay);
}
