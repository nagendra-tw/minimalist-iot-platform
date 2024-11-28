package com.learnings.iot_platform.dto.sensor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorUpdateRequestDto {
    @NotNull(message = "SensorId must be given")
    private String sensorId;

    @NotNull
    @Size(min = 2, message = "Sensor name length should be minimum 2 characters")
    private String sensorName;

    @NotNull
    @Min(value = 0, message = "Sensor temperature should be given in Kelvin and minimum is 0")
    private double temperature;

    @NotNull
    @Min(value = -90, message = "Latitude minimum value is -90")
    @Max(value = 90, message = "Latitude maximum value is 90")
    private double latitude;

    @NotNull
    @Min(value = -180, message = "Latitude minimum value is -180")
    @Max(value = 180, message = "Latitude maximum value is 180")
    private double longitude;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
