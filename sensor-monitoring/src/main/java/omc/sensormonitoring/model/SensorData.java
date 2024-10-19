package omc.sensormonitoring.model;

import jakarta.persistence.*;
import lombok.*;
import omc.sensormonitoring.model.composed.SensorDataId;

/**
 * Represents a record of temperature data collected from a sensor.
 * <p>
 * This entity is mapped to the database table and uses a composite primary key
 * defined by the {@link SensorDataId} class. Each instance of this class captures
 * the details of a temperature reading, including the sensor's unique identifier,
 * the timestamp of the reading, the direction the sensor is facing, and the recorded
 * temperature.
 * </p>
 */
@Entity
@ToString
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@IdClass(SensorDataId.class)
public class SensorData {

    /**
     * The unique identifier of the sensor.
     * This field is part of the composite primary key.
     */
    @Id
    private Long id;

    /**
     * The timestamp of the recorded temperature.
     * This field is part of the composite primary key.
     */
    @Id
    private long timestamp;

    /**
     * The face direction of the sensor from which the temperature is measured.
     */
    @Enumerated(EnumType.STRING)
    private FaceDirection face;

    /**
     * The temperature recorded by the sensor at the specified timestamp
     * for the given face direction.
     */
    private double temperature;

}