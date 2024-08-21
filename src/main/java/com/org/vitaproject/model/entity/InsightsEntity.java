package com.org.vitaproject.model.entity;

import com.org.vitaproject.util.JsonConverter;
import com.org.vitaproject.util.JsonConverterV2;
import com.org.vitaproject.util.JsonConverterV3;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Entity
@Table(name = "insights")
@AllArgsConstructor
@NoArgsConstructor

public class InsightsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Convert(converter = JsonConverter.class)
    @Column(name = "static_data", columnDefinition = "json")
    private Map<String, Object> staticData;
    @Column(name = "tests_list", columnDefinition = "json")
    @Convert(converter = JsonConverterV3.class)
    private List<String> testsList;
    @Convert(converter = JsonConverterV2.class)
    @Column(name = "bars_data", columnDefinition = "json")
    private Map<String, Map<String, Object>> barsData;

    @CreationTimestamp
    @Column(name = "date")
    private LocalDateTime date;

}
