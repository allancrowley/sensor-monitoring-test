package omc.sensorimitator.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import omc.sensorimitator.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Service
public class SensorImitatorServiceImpl implements SensorImitatorService {

    @Value("${sensors.count}")
    private int sensorsCount;

    @Value("${sensors.min.temperature}")
    private double minTemperature;

    @Value("${sensors.max.temperature}")
    private double maxTemperature;

    @Value("${sensors.min.deviation.temperature}")
    private double minDeviationTemperature;

    @Value("${sensors.max.deviation.temperature}")
    private double maxDeviationTemperature;

    @Value("${sensors.deviation.interval}")
    private int deviationSensorInterval;

    private Map<Long, FaceDirection> directionsMap;


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


    private double generateRandomTemperature(long sensorId) {
        if (sensorId % deviationSensorInterval == 0) {
            return ThreadLocalRandom.current().nextDouble(minDeviationTemperature, maxDeviationTemperature);
        }
        return ThreadLocalRandom.current().nextDouble(minTemperature, maxTemperature);
    }




}