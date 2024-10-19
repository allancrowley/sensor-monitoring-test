package omc.sensormonitoring.util;

import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.model.FaceDirection;
import omc.sensormonitoring.model.SensorData;

public class DataUtils {

    public static SensorDataDto getCorrectSensorData() {
        return new SensorDataDto(1l, 1000l, FaceDirection.NORTH, 36.6);
    }

    public static SensorDataDto getSensorDataMissingId() {
        return new SensorDataDto(null, 1000l, FaceDirection.NORTH, 36.6);
    }

    public static SensorDataDto getSensorDataMissingTimestamp() {
        return new SensorDataDto(1l, null, FaceDirection.NORTH, 36.6);
    }

    public static SensorDataDto getSensorDataMissingDirection() {
        return new SensorDataDto(1l, 1000l, null, 36.6);
    }

    public static SensorDataDto getSensorDataMissingTemperature() {
        return new SensorDataDto(1l, 1000l, FaceDirection.NORTH, null);
    }

    public static long getStartOfPeriod() {
        return 1000l;
    }

    public static SensorData getSensorData() {
        return new SensorData(1l, 1000l, FaceDirection.NORTH, 36.6);
    }
}
