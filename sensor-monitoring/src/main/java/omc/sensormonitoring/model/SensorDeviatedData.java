package omc.sensormonitoring.model;

import jakarta.persistence.*;
import lombok.*;
import omc.sensormonitoring.model.composed.SensorDataId;


/**
 * Represents a record of sensor data that has deviated from the expected temperature range.
 * <p>
 * This entity is mapped to the database table and uses a composite primary key
 * defined by the {@link SensorDataId} class. Each instance of this class captures
 * the details of a sensor reading that has been identified as deviated based on
 * predefined thresholds, including the sensor's identification, timestamp of the reading,
 * face direction, and the temperature measured.
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
public class SensorDeviatedData {

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
     * The temperature recorded by the sensor that has deviated from the expected range.
     */
    private double temperature;

}
