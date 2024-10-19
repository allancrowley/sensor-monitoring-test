package omc.sensormonitoring.model;

import jakarta.persistence.*;
import lombok.*;
import omc.sensormonitoring.model.composed.SensorFaceId;

/**
 * Represents the average temperature data for a specific face direction of a sensor
 * at a given timestamp.
 * <p>
 * This entity is mapped to the database table and uses a composite primary key
 * defined by the {@link SensorFaceId} class. Each instance of this class holds
 * the average temperature measured by a sensor, categorized by its face direction
 * and the timestamp of the measurement.
 * </p>
 */
@Entity
@ToString
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@IdClass(SensorFaceId.class)
public class SensorFaceData {

    /**
     * The timestamp of the recorded temperature.
     * This field is part of the composite primary key.
     */
    @Id
    private long timestamp;

    /**
     * The face direction of the sensor from which the temperature is measured.
     * This field is part of the composite primary key.
     */
    @Id
    @Enumerated(EnumType.STRING)
    private FaceDirection face;

    /**
     * The average temperature recorded by the sensor at the specified timestamp
     * for the given face direction.
     */
    private double temperature;
}