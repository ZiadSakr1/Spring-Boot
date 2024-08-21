package com.org.vitaproject.repository;

import com.org.vitaproject.model.dto.TestsResultDTO;
import com.org.vitaproject.model.dto.TestsValueDTO;
import com.org.vitaproject.model.entity.TestsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestsRepo extends JpaRepository<TestsEntity, Long> {
    List<TestsEntity> findAllByPatientName(String patientName);

    @Query("select distinct(p.description)  from TestsEntity p where p.category=:category and " +
            " p.patientName=:patientName")
    List<String> findAllByDescription(String category,String patientName);

    @Query("select distinct(p.category)  from TestsEntity p where p.patientName=:patientName")
    List<String> findAllCategoryPatientName(String patientName);

    @Query("select max(p.ID) from TestsEntity p where p.description=:description and p.patientName=:patientName")
    Long findByDescription(String description,String patientName);

    @Query("select new com.org.vitaproject.model.dto.TestsValueDTO(p.value, p.createdAt)  " +
            "from TestsEntity p where p.description=:description and p.patientName =:patientName " +
            "order by p.createdAt desc ")
    List<TestsValueDTO> findAllValues(String description, String patientName);


}
