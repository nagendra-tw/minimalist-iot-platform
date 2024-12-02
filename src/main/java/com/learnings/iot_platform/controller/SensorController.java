package com.learnings.iot_platform.controller;

import com.learnings.iot_platform.dto.*;
import com.learnings.iot_platform.dto.sensor.CreateSensorRequestDto;
import com.learnings.iot_platform.dto.sensor.DeleteSensorResponseDto;
import com.learnings.iot_platform.dto.sensor.SensorResponseDto;
import com.learnings.iot_platform.dto.sensor.UpdateSensorRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensors")
public class SensorController {
    @Autowired
    private SensorService sensorService;

    @GetMapping
    public ResponseEntity<List<SensorResponseDto>> getAllSensors() {
        return new ResponseEntity<>(sensorService.getAllSensors(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSensorById(@PathVariable String id) {
        try {
            return new ResponseEntity<>(sensorService.getSensorById(id), HttpStatus.OK);
        } catch(SensorNotFoundException e) {

            return new ResponseEntity<>(new ApiResponse("Sensor not found with id: " + id), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<SensorResponseDto> createSensor(@Valid @RequestBody CreateSensorRequestDto createSensorRequestDto) {
        SensorResponseDto savedSensor = sensorService.createSensor(createSensorRequestDto);
        return new ResponseEntity<>(savedSensor, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSensorResponseDto> deleteSensor(@PathVariable String id) {
        try {
            sensorService.deleteSensor(id);
            DeleteSensorResponseDto deleteResponseDto = new DeleteSensorResponseDto("Sensor deleted with id: " + id);

            return new ResponseEntity<>(new DeleteSensorResponseDto("Sensor deleted with id: " + id), HttpStatus.OK);
        } catch(SensorNotFoundException e) {
            return new ResponseEntity<>(new DeleteSensorResponseDto("Sensor not found with id: " + id), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateSensor(@Valid @RequestBody UpdateSensorRequestDto updateSensorRequestDto) {
        try {
            SensorResponseDto sensorResponseDto = sensorService.updateSensor(updateSensorRequestDto);
            return new ResponseEntity<>(sensorResponseDto, HttpStatus.OK);
        } catch(SensorNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse("Sensor not found with id: " + updateSensorRequestDto.getSensorId()), HttpStatus.NOT_FOUND);
        }
    }

}
