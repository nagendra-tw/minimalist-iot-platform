package com.learnings.iot_platform.dto.sensor;

import com.learnings.iot_platform.model.SensorData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorResponseDto {
    private String id;
    private String name;
    private double temperature;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SensorData sensorData;
}
