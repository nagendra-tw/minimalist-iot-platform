package com.learnings.iot_platform.unit.producer;

import com.learnings.iot_platform.constants.Constants;
import com.learnings.iot_platform.events.SensorDataStoredEvent;
import com.learnings.iot_platform.model.SensorData;
import com.learnings.iot_platform.producer.SensorDataProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorDataProducerServiceTest {


    @Mock
    KafkaTemplate<String, SensorDataStoredEvent> kafkaTemplate;


    SensorDataProducerService sensorDataProducerService;

    @BeforeEach
    void setUp() {
        sensorDataProducerService = new SensorDataProducerService(kafkaTemplate);
    }

    @Test
    void givenSensorData_whenSensorDataStored_thenProduceSensorDataStoredEvent() {
        SensorData sensorData = new SensorData();
        sensorData.setSensorId("valid-sensor-id");
        sensorData.setTemperature(50d);
        sensorData.setBattery(65d);
        sensorData.setLatitude(17d);
        sensorData.setLongitude(18d);
        sensorData.setTimestamp(LocalDateTime.now());
        sensorData.setSensorDataId("1");

        SensorDataStoredEvent expectedEvent = new SensorDataStoredEvent();
        expectedEvent.setBattery(sensorData.getBattery());
        expectedEvent.setTemperature(sensorData.getTemperature());
        expectedEvent.setLatitude(sensorData.getLatitude());
        expectedEvent.setLongitude(sensorData.getLongitude());
        expectedEvent.setSensorId(sensorData.getSensorId());

        sensorDataProducerService.produceSensorDataStoredEvent(sensorData);

        verify(kafkaTemplate, times(1))
                .send(
                        eq(Constants.SENSOR_DATA_STORED_EVENT_TOPIC),
                        eq(sensorData.getSensorId()),
                        eq(expectedEvent)
                );
    }
}
