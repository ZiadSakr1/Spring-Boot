package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.OrganizationProfileDTO;
import com.org.vitaproject.model.entity.OrganizationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationEntity toOrganizationEntity(OrganizationProfileDTO organizationProfileDTO);
}
