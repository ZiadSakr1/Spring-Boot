package com.org.vitaproject.repository;

import com.org.vitaproject.model.dto.BarsDataDTO;
import com.org.vitaproject.model.dto.StaticDataDTO;
import com.org.vitaproject.model.dto.TestsListDTO;
import com.org.vitaproject.model.entity.InsightsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;

public interface InsightsRepo extends JpaRepository<InsightsEntity, Long> {
    @Query("select new com.org.vitaproject.model.dto.StaticDataDTO(p.staticData) from InsightsEntity p ORDER BY p.id desc limit 1")
    StaticDataDTO getStaticData();
    @Query("select new com.org.vitaproject.model.dto.TestsListDTO(p.testsList) from InsightsEntity p ORDER BY p.id desc limit 1")
    TestsListDTO getTestsList();
    @Query("select new com.org.vitaproject.model.dto.BarsDataDTO(p.barsData) from InsightsEntity p ORDER BY p.id desc limit 1")
    BarsDataDTO getBarsData();
}
