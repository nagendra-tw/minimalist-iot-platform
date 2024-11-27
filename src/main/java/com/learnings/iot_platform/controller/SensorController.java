package com.learnings.iot_platform.controller;

import com.learnings.iot_platform.dto.SensorDeleteResponseDto;
import com.learnings.iot_platform.dto.SensorRequestDto;
import com.learnings.iot_platform.dto.SensorResponseDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @PostMapping
    public ResponseEntity<SensorResponseDto> createSensor(@RequestBody SensorRequestDto sensorRequestDto) {
        SensorResponseDto savedSensor = sensorService.createSensor(sensorRequestDto);
        return new ResponseEntity<>(savedSensor, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SensorDeleteResponseDto> deleteSensor(@PathVariable String id) {
        try {
            sensorService.deleteSensor(id);
            return new ResponseEntity<>(new SensorDeleteResponseDto("Sensor deleted with id: " + id), HttpStatus.NO_CONTENT);
        } catch(SensorNotFoundException e) {
            return new ResponseEntity<>(new SensorDeleteResponseDto("Sensor not found with id: " + id), HttpStatus.NOT_FOUND);
        }
    }

}
