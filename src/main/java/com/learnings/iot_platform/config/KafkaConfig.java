package com.learnings.iot_platform.config;

import com.learnings.iot_platform.constants.Constants;
import com.learnings.iot_platform.events.SensorDataStoredEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;


    Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:8082");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return config;
    }

    @Bean
    public ProducerFactory<String, SensorDataStoredEvent> sensorDataStoredEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, SensorDataStoredEvent> kafkaTemplate() {
        return new KafkaTemplate<>(sensorDataStoredEventProducerFactory());
    }

    @Bean
    NewTopic createSensorDataStoredEventTopic() {
        System.out.println("going to create topic");
        return TopicBuilder
                .name(Constants.SENSOR_DATA_STORED_EVENT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

}
