package com.learnings.iot_platform.repository;

import com.learnings.iot_platform.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends MongoRepository<SensorData, String> {
}
