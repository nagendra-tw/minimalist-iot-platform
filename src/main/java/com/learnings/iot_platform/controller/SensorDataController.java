package com.learnings.iot_platform.controller;

import com.learnings.iot_platform.dto.ApiResponse;
import com.learnings.iot_platform.dto.sensordata.CreateSensorDataRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.service.SensorDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensors-data")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @PostMapping
    public ResponseEntity<?> storeSensorData(@RequestBody CreateSensorDataRequestDto createSensorDataRequestDto) {
        try {
            sensorDataService.storeSensorData(createSensorDataRequestDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (SensorNotFoundException e) {
            ApiResponse apiResponse = new ApiResponse(e.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
