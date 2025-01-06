package com.learnings.iot_platform.repository;

import com.learnings.iot_platform.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorDataRepository extends MongoRepository<SensorData, String> {
    Optional<SensorData> findTopBySensorIdOrderByTimestampDesc(String sensorId);
}
