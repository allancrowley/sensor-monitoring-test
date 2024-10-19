package omc.sensormonitoring.model;

import jakarta.persistence.*;
import lombok.*;
import omc.sensormonitoring.model.composed.SensorFaceId;


@Entity
@ToString
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@IdClass(SensorFaceId.class)
public class SensorFaceData {

    @Id
    private long timestamp;

    @Id
    @Enumerated(EnumType.STRING)
    private FaceDirection face;

    private double temperature;
}