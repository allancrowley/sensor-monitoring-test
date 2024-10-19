package omc.sensormonitoring.model.composed;

import omc.sensormonitoring.model.FaceDirection;
import omc.sensormonitoring.model.SensorFaceData;

import java.io.Serializable;
import java.util.Objects;


public class SensorFaceId implements Serializable {

    private FaceDirection face;
    private long timestamp;

    public SensorFaceId() {}

    public SensorFaceId(FaceDirection face, long timestamp) {
        this.face = face;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorFaceId)) return false;
        SensorFaceId that = (SensorFaceId) o;
        return timestamp == that.timestamp && Objects.equals(face, that.face);
    }

    @Override
    public int hashCode() {
        return Objects.hash(face, timestamp);
    }
}