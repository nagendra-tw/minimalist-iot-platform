package com.learnings.iot_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorRequestDto {
    private String name;
    private double temperature;
    private double latitude;
    private double longitude;
}
