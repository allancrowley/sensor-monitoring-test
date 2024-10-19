package omc.sensorimitator.service;

import omc.sensorimitator.dto.SensorDataDto;

import java.util.List;

/**
 * Service interface for generating random sensor data.
 * Implementations of this interface are responsible for providing
 * simulated sensor data, including temperature and face direction.
 */
public interface SensorImitatorService {

    /**
     * Generates a list of random sensor data entries.
     * Each sensor will have an ID, a timestamp, a face direction, and a temperature value.
     * The temperature may deviate for certain sensors as defined by the implementation.
     *
     * @return A list of {@link SensorDataDto} representing the random sensor data.
     */
    List<SensorDataDto> getRandomSensorData();
}
