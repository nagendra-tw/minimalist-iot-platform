package com.learnings.iot_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorUpdateRequestDto {
    private String sensorId;
    private String sensorName;
    private double temperature;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
