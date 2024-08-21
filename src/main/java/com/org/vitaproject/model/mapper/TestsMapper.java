package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.TestsResultDTO;
import com.org.vitaproject.model.entity.TestsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestsMapper {
    TestsEntity toTestsEntity(TestsResultDTO testsResultDTO);

    TestsResultDTO toTestsResultDto(TestsEntity tests);
}
