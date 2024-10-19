package omc.sensorimitator.dto;


public record SensorDataDto(
        long id,
        long timestamp,
        FaceDirection face,
        double temperature
) {}
