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

    @Id
    private Long id;

    @Id
    private long timestamp;

    @Enumerated(EnumType.STRING)
    private FaceDirection face;

    private double temperature;

}
