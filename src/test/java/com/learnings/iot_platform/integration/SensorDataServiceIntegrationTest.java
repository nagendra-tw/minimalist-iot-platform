package com.learnings.iot_platform.integration;

import com.learnings.iot_platform.constants.Constants;
import com.learnings.iot_platform.dto.sensordata.CreateSensorDataRequestDto;
import com.learnings.iot_platform.events.SensorDataStoredEvent;
import com.learnings.iot_platform.repository.SensorDataRepository;
import com.learnings.iot_platform.repository.SensorRepository;
import com.learnings.iot_platform.service.SensorDataService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
@AutoConfigureMockMvc
@Testcontainers
public class SensorDataServiceIntegrationTest {

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private SensorRepository sensorRepository;

    @Autowired
    Environment environment;

    private KafkaMessageListenerContainer<String, SensorDataStoredEvent> container;
    private BlockingQueue<ConsumerRecord<String, SensorDataStoredEvent>> records;


    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4")
            .withExposedPorts(27017)
            .waitingFor(Wait.forListeningPort());

//    @DynamicPropertySource
//    static void mongoProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//    }

    @Test
    void testDatabaseConnection() {
        assertTrue(mongoDBContainer.isRunning(), "Mongodb container should be running");
    }


    @BeforeAll
    void setUp() {
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());

        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(Constants.SENSOR_DATA_STORED_EVENT_TOPIC);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, SensorDataStoredEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    void givenCreateSensorDataDto_whenSensorDataStored_thenProduceSensorDataStoredEvent() throws InterruptedException, ExecutionException {
        CreateSensorDataRequestDto createSensorDataRequestDto = new CreateSensorDataRequestDto();
        createSensorDataRequestDto.setSensorId("valid-sensor-id");
        createSensorDataRequestDto.setLatitude(17d);
        createSensorDataRequestDto.setLongitude(18d);
        createSensorDataRequestDto.setBattery(10d);
        createSensorDataRequestDto.setTemperature(20d);

        when(sensorRepository.existsById("valid-sensor-id")).thenReturn(true);

        sensorDataService.storeSensorData(createSensorDataRequestDto);

        ConsumerRecord<String, SensorDataStoredEvent> record = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(record);
        assertNotNull(record.key());
        SensorDataStoredEvent storedEvent = (SensorDataStoredEvent) record.value();
        assertEquals(createSensorDataRequestDto.getSensorId(), storedEvent.getSensorId());
        assertEquals(createSensorDataRequestDto.getLatitude(), storedEvent.getLatitude());
        assertEquals(createSensorDataRequestDto.getLongitude(), storedEvent.getLongitude());
        assertEquals(createSensorDataRequestDto.getBattery(), storedEvent.getBattery());
        assertEquals(createSensorDataRequestDto.getTemperature(), storedEvent.getTemperature());
    }


    @AfterAll
    void tearDown() {
        container.stop();
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                JsonDeserializer.TRUSTED_PACKAGES,"*",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset")
        );
    }
}
