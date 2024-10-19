package omc.sensorimitator.dto;

/**
 * A data transfer object (DTO) representing sensor data.
 * This record contains the sensor's ID, the timestamp of the data,
 * the direction the sensor is facing, and the measured temperature.
 */
public record SensorDataDto(
        /**
         * The unique identifier of the sensor.
         */
        long id,

        /**
         * The timestamp of when the sensor data was generated.
         * Represented as the number of milliseconds since the epoch (January 1, 1970).
         */
        long timestamp,

        /**
         * The direction in which the sensor is facing, represented by an enum {@link FaceDirection}.
         */
        FaceDirection face,

        /**
         * The temperature recorded by the sensor, which may vary based on certain conditions.
         */
        double temperature
) {}
