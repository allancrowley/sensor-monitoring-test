package omc.sensormonitoring.model;

import jakarta.persistence.*;
import lombok.*;
import omc.sensormonitoring.model.composed.SensorDataId;


@Entity
@ToString
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@IdClass(SensorDataId.class)
public class SensorData {

    @Id
    private Long id;

    @Id
    private long timestamp;

    @Enumerated(EnumType.STRING)
    private FaceDirection face;

    private double temperature;

}