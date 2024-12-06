package com.learnings.iot_platform.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataStoredEvent {
    private String sensorId;
    private double temperature;
    private double latitude;
    private double longitude;
    private double battery;
}