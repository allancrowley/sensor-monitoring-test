package omc.sensormonitoring.dto;


import omc.sensormonitoring.model.FaceDirection;
import jakarta.validation.constraints.NotNull;
import static omc.sensormonitoring.controller.handler.ErrorMessages.*;


/**
 * A data transfer object (DTO) representing sensor data.
 * This record contains the sensor's ID, the timestamp of the data,
 * the direction the sensor is facing, and the measured temperature.
 */
public record SensorDataDto(
        /**
         * The unique identifier of the sensor.
         */
        @NotNull(message=MISSING_SENSOR_ID_MESSAGE)
        Long id,

        /**
         * The timestamp of when the sensor data was generated.
         * Represented as the number of milliseconds since the epoch (January 1, 1970).
         */
        @NotNull(message=MISSING_SENSOR_TIMESTAMP_MESSAGE)
        Long timestamp,

        /**
         * The direction in which the sensor is facing, represented by an enum {@link FaceDirection}.
         */
        @NotNull(message=MISSING_SENSOR_FACE_DIRECTION_MESSAGE)
        FaceDirection face,

        /**
         * The temperature recorded by the sensor, which may vary based on certain conditions.
         */
        @NotNull(message=MISSING_SENSOR_TEMPERATURE_MESSAGE)
        Double temperature
) {}
