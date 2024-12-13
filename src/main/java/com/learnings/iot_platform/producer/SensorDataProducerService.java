package com.learnings.iot_platform.producer;


import com.learnings.iot_platform.constants.Constants;
import com.learnings.iot_platform.events.SensorDataStoredEvent;
import com.learnings.iot_platform.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class SensorDataProducerService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, SensorDataStoredEvent> kafkaTemplate;

    public SensorDataProducerService(KafkaTemplate<String, SensorDataStoredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceSensorDataStoredEvent(SensorData sensorData) throws ExecutionException, InterruptedException {
        SensorDataStoredEvent sensorDataStoredEvent = new SensorDataStoredEvent();
        sensorDataStoredEvent.setSensorId(sensorData.getSensorId());
        sensorDataStoredEvent.setBattery(sensorData.getBattery());
        sensorDataStoredEvent.setTemperature(sensorData.getTemperature());
        sensorDataStoredEvent.setLatitude(sensorData.getLatitude());
        sensorDataStoredEvent.setLongitude(sensorData.getLongitude());
        sensorDataStoredEvent.setTimestamp(sensorData.getTimestamp());
        sensorDataStoredEvent.setSensorDataId(sensorData.getSensorDataId());


        CompletableFuture<SendResult<String, SensorDataStoredEvent>> future = kafkaTemplate.send(
                Constants.SENSOR_DATA_STORED_EVENT_TOPIC,
                sensorData.getSensorId(),
                sensorDataStoredEvent
        );


        String sensorId = sensorData.getSensorId();

        System.out.println("Sensor ID: " + sensorId);
        System.out.println("String Hash Code: " + sensorId.hashCode());
        System.out.println("Abs Hash Code: " + Math.abs(sensorId.hashCode()));
        System.out.println("Partition (% 3): " + (Math.abs(sensorId.hashCode()) % 3));
        System.out.println("---");


        future.thenAccept(result ->
                System.out.println("Sent message to partition: " + result.getRecordMetadata().partition())
        ).exceptionally(ex -> {
            System.err.println("Error sending message: " + ex);
            return null;
        });

        LOGGER.info("Published sensor data stored event: {}", sensorDataStoredEvent);
    }
}
