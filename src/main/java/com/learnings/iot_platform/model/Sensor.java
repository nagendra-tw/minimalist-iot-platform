package com.learnings.iot_platform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sensors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    private String id;
    private String name;
    private Double temperature;
    private Double latitude;
    private Double longitude;

}
