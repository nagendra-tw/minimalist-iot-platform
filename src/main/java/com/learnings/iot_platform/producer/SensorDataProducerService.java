package com.learnings.iot_platform.producer;


import com.learnings.iot_platform.constants.Constants;
import com.learnings.iot_platform.events.SensorDataStoredEvent;
import com.learnings.iot_platform.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SensorDataProducerService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, SensorDataStoredEvent> kafkaTemplate;

    public SensorDataProducerService(KafkaTemplate<String, SensorDataStoredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceSensorDataStoredEvent(SensorData sensorData) {
        SensorDataStoredEvent sensorDataStoredEvent = new SensorDataStoredEvent();
        sensorDataStoredEvent.setSensorId(sensorData.getSensorId());
        sensorDataStoredEvent.setBattery(sensorData.getBattery());
        sensorDataStoredEvent.setTemperature(sensorData.getTemperature());
        sensorDataStoredEvent.setLatitude(sensorData.getLatitude());
        sensorDataStoredEvent.setLongitude(sensorData.getLongitude());
        sensorDataStoredEvent.setTimestamp(sensorData.getTimestamp());
        sensorDataStoredEvent.setSensorDataId(sensorData.getSensorDataId());

        LOGGER.info("Going to publish sensor data stored event: {}", sensorDataStoredEvent);

        kafkaTemplate.send(Constants.SENSOR_DATA_STORED_EVENT_TOPIC, sensorData.getSensorId(), sensorDataStoredEvent);

        LOGGER.info("Published sensor data stored event: {}", sensorDataStoredEvent);
    }
}
