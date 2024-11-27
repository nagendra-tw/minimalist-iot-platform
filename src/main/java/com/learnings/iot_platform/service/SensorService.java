package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.SensorRequestDto;
import com.learnings.iot_platform.dto.SensorResponseDto;
import com.learnings.iot_platform.dto.SensorUpdateRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public SensorResponseDto createSensor(SensorRequestDto sensorRequestDto) {
        Sensor convertedSensor = convertSensorRequestDtoToSensor(sensorRequestDto);
        Sensor savedSensor = sensorRepository.save(convertedSensor);
        return convertSensorToSensorResponse(savedSensor);
    }

    public List<SensorResponseDto> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();
        return sensors.stream().map(this::convertSensorToSensorResponse).toList();
    }

    public SensorResponseDto getSensorById(String sensorId) {
        Sensor sensor = getSensor(sensorId);

        return convertSensorToSensorResponse(sensor);
    }

    public SensorResponseDto updateSensor(SensorUpdateRequestDto sensorUpdateRequestDto) {
        Sensor savedSensor = getSensor(sensorUpdateRequestDto.getSensorId());

        savedSensor.setName(sensorUpdateRequestDto.getSensorName());
        savedSensor.setTemperature(sensorUpdateRequestDto.getTemperature());
        savedSensor.setLatitude(sensorUpdateRequestDto.getLatitude());
        savedSensor.setLongitude(sensorUpdateRequestDto.getLongitude());
        Sensor updatedSensor = sensorRepository.save(savedSensor);
        return convertSensorToSensorResponse(updatedSensor);
    }

    public void deleteSensor(String sensorId) {
        Sensor existingSensor = getSensor(sensorId);
        sensorRepository.deleteById(existingSensor.getId());
    }

    private Sensor getSensor(String sensorId) {
        return sensorRepository.findById(sensorId).orElseThrow(() -> new SensorNotFoundException(sensorId));
    }

    private Sensor convertSensorRequestDtoToSensor(SensorRequestDto sensorRequestDto) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorRequestDto.getName());
        sensor.setTemperature(sensorRequestDto.getTemperature());
        sensor.setLatitude(sensorRequestDto.getLatitude());
        sensor.setLongitude(sensorRequestDto.getLongitude());
        return sensor;
    }

    private SensorResponseDto convertSensorToSensorResponse(Sensor sensor) {
        SensorResponseDto sensorResponseDto = new SensorResponseDto();
        sensorResponseDto.setId(sensor.getId());
        sensorResponseDto.setName(sensor.getName());
        sensorResponseDto.setTemperature(sensor.getTemperature());
        sensorResponseDto.setLatitude(sensor.getLatitude());
        sensorResponseDto.setLongitude(sensor.getLongitude());
        return sensorResponseDto;
    }

}
