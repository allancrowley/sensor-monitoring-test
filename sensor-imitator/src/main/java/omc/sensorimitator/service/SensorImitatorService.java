package omc.sensorimitator.service;

import omc.sensorimitator.dto.SensorDataDto;

import java.util.List;

public interface SensorImitatorService {

    List<SensorDataDto> getRandomSensorData();
}
