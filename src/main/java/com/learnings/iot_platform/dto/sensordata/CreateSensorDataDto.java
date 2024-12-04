package com.learnings.iot_platform.dto.sensordata;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSensorDataDto {
    @NotNull(message = "sensorId should be provided")
    private String sensorId;

    @NotNull
    @Min(value = 0, message = "Sensor data temperature should be given in Kelvin and minimum is 0")
    private double temperature;

    @NotNull
    @Min(value = -90, message = "Latitude minimum value is -90")
    @Max(value = 90, message = "Latitude maximum value is 90")
    private double latitude;

    @NotNull
    @Min(value = -180, message = "Latitude minimum value is -180")
    @Max(value = 180, message = "Latitude maximum value is 180")
    private double longitude;

    @NotNull
    @Min(value = 0, message = "Battery minimum value is 0")
    @Max(value = 100, message = "Battery maximum value is 0")
    private Double battery;


}
