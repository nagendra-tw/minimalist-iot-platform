package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.SensorRequestDto;
import com.learnings.iot_platform.dto.SensorResponseDto;
import com.learnings.iot_platform.dto.SensorUpdateRequestDto;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

@Service
public class SensorService {

    private SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public Sensor createSensor(SensorRequestDto sensorRequestDto) {
        Sensor convertedSensor = convertSensorRequestDtoToSensor(sensorRequestDto);
        return sensorRepository.save(convertedSensor);
    }

    public SensorResponseDto getSensorById(String sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElse(null);
        System.out.println(sensor);
        if (sensor == null) {
            // todo: show detailed information in the response
            return null;
        }
        return convertSensorToSensorResponse(sensor);
    }

    public SensorResponseDto updateSensor(SensorUpdateRequestDto sensorUpdateRequestDto) {
        Sensor savedSensor = sensorRepository.findById(sensorUpdateRequestDto.getSensorId()).orElse(null);
        System.out.println("savedSensor: " + savedSensor);
        if(savedSensor == null) {
            // todo: raise an error
            return null;
        }
        savedSensor.setName(sensorUpdateRequestDto.getSensorName());
        savedSensor.setTemperature(sensorUpdateRequestDto.getTemperature());
        savedSensor.setLatitude(sensorUpdateRequestDto.getLatitude());
        savedSensor.setLongitude(sensorUpdateRequestDto.getLongitude());
        Sensor updatedSensor = sensorRepository.save(savedSensor);
        return convertSensorToSensorResponse(updatedSensor);
    }

    public void deleteSensor(String sensorId) {
        if(sensorRepository.existsById(sensorId)) {
            sensorRepository.deleteById(sensorId);
        }
        // todo: throw an error if sensor with given sensorId does not exist
    }

    private Sensor convertSensorRequestDtoToSensor(SensorRequestDto sensorRequestDto) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorRequestDto.getSensorName());
        sensor.setTemperature(sensorRequestDto.getTemperature());
        sensor.setLatitude(sensorRequestDto.getLatitude());
        sensor.setLongitude(sensorRequestDto.getLongitude());
        return sensor;
    }

    private SensorResponseDto convertSensorToSensorResponse(Sensor sensor) {
        SensorResponseDto sensorResponseDto = new SensorResponseDto();
        sensorResponseDto.setSensorId(sensor.getId());
        sensorResponseDto.setSensorName(sensor.getName());
        sensorResponseDto.setTemperature(sensor.getTemperature());
        sensorResponseDto.setLatitude(sensor.getLatitude());
        sensorResponseDto.setLongitude(sensor.getLongitude());
        return sensorResponseDto;
    }


}
