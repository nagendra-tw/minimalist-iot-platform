package com.learnings.iot_platform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "sensors-data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorData {
    @Id
    private String sensorDataId;
    private String sensorId;
    private Double temperature;
    private Double latitude;
    private Double longitude;
    private Double battery;
    private LocalDateTime timestamp;
}
