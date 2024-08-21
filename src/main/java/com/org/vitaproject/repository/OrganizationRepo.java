package com.org.vitaproject.repository;

import com.org.vitaproject.model.dto.OrganizationProfileDTO;
import com.org.vitaproject.model.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganizationRepo extends JpaRepository<OrganizationEntity, String> {
    @Query("select new com.org.vitaproject.model.dto.OrganizationProfileDTO(p.organizationName,p.email" +
            ",p.location,p.phone) from OrganizationEntity p where p.organizationName =:organizationName")
    OrganizationProfileDTO findByOrganizationName(String organizationName);
}
