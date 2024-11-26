package com.learnings.iot_platform.repository;

import com.learnings.iot_platform.model.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends MongoRepository<Sensor, String> {
}
