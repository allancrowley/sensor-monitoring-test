package omc.sensormonitoring.service;

import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.model.SensorDeviatedData;
import omc.sensormonitoring.model.SensorFaceData;
import java.util.List;


public interface SensorService {

    void saveSensorData(SensorDataDto sensorDataDto);

    List<SensorFaceData> getAvgFaceDirectionTemperatures(long startOfPeriod);

    List<SensorDeviatedData> getMalfunctioningSensors();
}
