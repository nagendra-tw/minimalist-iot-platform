package com.learnings.iot_platform.controller;

import com.learnings.iot_platform.dto.*;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.service.SensorService;
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
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse("Sensor not found with id: " + id), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<SensorResponseDto> createSensor(@RequestBody SensorCreateRequestDto sensorCreateRequestDto) {
        SensorResponseDto savedSensor = sensorService.createSensor(sensorCreateRequestDto);
        return new ResponseEntity<>(savedSensor, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SensorDeleteResponseDto> deleteSensor(@PathVariable String id) {
        try {
            sensorService.deleteSensor(id);
            SensorDeleteResponseDto deleteResponseDto = new SensorDeleteResponseDto("Sensor deleted with id: " + id);
            System.out.println(deleteResponseDto);
            return new ResponseEntity<>(new SensorDeleteResponseDto("Sensor deleted with id: " + id), HttpStatus.OK);
        } catch(SensorNotFoundException e) {
            return new ResponseEntity<>(new SensorDeleteResponseDto("Sensor not found with id: " + id), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateSensor(@RequestBody SensorUpdateRequestDto sensorUpdateRequestDto) {
        try {
            SensorResponseDto sensorResponseDto = sensorService.updateSensor(sensorUpdateRequestDto);
            return new ResponseEntity<>(sensorResponseDto, HttpStatus.OK);
        } catch(SensorNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse("Sensor not found with id: " + sensorUpdateRequestDto.getSensorId()), HttpStatus.NOT_FOUND);
        }
    }

}
