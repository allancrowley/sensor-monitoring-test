package omc.sensorimitator.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import omc.sensorimitator.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Service implementation for generating random sensor data and simulating
 * face direction for sensors.
 */
@Slf4j
@Service
public class SensorImitatorServiceImpl implements SensorImitatorService {

    /**
     * The total number of sensors to simulate.
     */
    @Value("${sensors.count}")
    private int sensorsCount;

    /**
     * The minimum temperature for sensors generating standard data.
     */
    @Value("${sensors.min.temperature}")
    private double minTemperature;

    /**
     * The maximum temperature for sensors generating standard data.
     */
    @Value("${sensors.max.temperature}")
    private double maxTemperature;

    /**
     * The minimum temperature for sensors generating deviation data (outliers).
     */
    @Value("${sensors.min.deviation.temperature}")
    private double minDeviationTemperature;

    /**
     * The maximum temperature for sensors generating deviation data (outliers).
     */
    @Value("${sensors.max.deviation.temperature}")
    private double maxDeviationTemperature;

    /**
     * The interval for determining which sensors will generate deviation (outlier) data.
     * Every nth sensor, where n is the deviation interval, will generate outlier data.
     */
    @Value("${sensors.deviation.interval}")
    private int deviationSensorInterval;

    /**
     * A map of sensor IDs to their corresponding face directions.
     * This map is immutable after initialization.
     */
    private Map<Long, FaceDirection> directionsMap;


    /**
     * Initializes the {@code directionsMap} after the bean's properties have been set.
     * Each sensor is assigned a random {@link FaceDirection}.
     */
    @PostConstruct
    private void createDirectionMap() {
        FaceDirection[] faceDirections = FaceDirection.values();
        directionsMap = new HashMap<>();
        for (long i = 1; i <= sensorsCount; i++) {
            directionsMap.put(i, faceDirections[ThreadLocalRandom.current().nextInt(faceDirections.length)]);
        }
        log.debug("Created direction map for {} sensors", directionsMap.size());
        directionsMap = Collections.unmodifiableMap(directionsMap);
    }


    /**
     * Generates random sensor data for all simulated sensors.
     * Sensors that are at a multiple of the {@code deviationSensorInterval} will
     * generate deviation temperature data, while others will generate standard temperature data.
     *
     * @return A list of {@link SensorDataDto} representing the sensor data for all sensors.
     */
    @Override
    public List<SensorDataDto> getRandomSensorData() {
        List<SensorDataDto> sensorDataList = new ArrayList<>();

        for (long i = 1; i <= sensorsCount; i++) {
            double temperature = generateRandomTemperature(i);
            sensorDataList.add(new SensorDataDto(i, System.currentTimeMillis(), directionsMap.get(i), temperature));
        }
        log.debug("Generated {} sensor data entries", sensorDataList.size());
        return sensorDataList;
    }


    /**
     * Generates a random temperature for the given sensor.
     * If the sensor ID is a multiple of {@code deviationSensorInterval}, it will generate
     * a temperature within the deviation range. Otherwise, it generates a standard temperature.
     *
     * @param sensorId The ID of the sensor for which to generate the temperature.
     * @return A random temperature based on the sensor's ID.
     */
    private double generateRandomTemperature(long sensorId) {
        if (sensorId % deviationSensorInterval == 0) {
            return ThreadLocalRandom.current().nextDouble(minDeviationTemperature, maxDeviationTemperature);
        }
        return ThreadLocalRandom.current().nextDouble(minTemperature, maxTemperature);
    }

}