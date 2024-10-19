package omc.sensormonitoring.service;

import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.model.*;
import java.util.List;

/**
 * Service interface for managing sensor data operations.
 * <p>
 * This interface defines the methods required for saving sensor data, retrieving average face direction
 * temperatures, and obtaining malfunctioning sensors.
 * </p>
 */
public interface SensorService {

    /**
     * Saves the provided sensor data to the system.
     *
     * @param sensorDataDto the sensor data to be saved
     */
    void saveSensorData(SensorDataDto sensorDataDto);

    /**
     * Retrieves average temperatures for each face direction over a specified time period.
     *
     * @param startOfPeriod the start of the time period (in milliseconds since epoch)
     * @return a list of {@link SensorFaceData} containing average temperatures for each face direction
     */
    List<SensorFaceData> getAvgFaceDirectionTemperatures(long startOfPeriod);

    /**
     * Retrieves a list of malfunctioning sensors detected in the system.
     *
     * @return a list of {@link SensorDeviatedData} representing the malfunctioning sensors
     */
    List<SensorDeviatedData> getMalfunctioningSensors();

    /**
     * Scheduled task that calculates and stores hourly average sensor data.
     */
    void calculateAndStoreHourlyAverageData();
}
