package omc.sensormonitoring.model.composed;

import omc.sensormonitoring.model.FaceDirection;
import omc.sensormonitoring.model.SensorFaceData;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the composite primary key for the {@link SensorFaceData} entity.
 * <p>
 * This class encapsulates the unique identifiers for a sensor's face direction
 * and the corresponding timestamp of the temperature reading. It implements
 * {@link Serializable} to allow instances of this class to be serialized,
 * which is required for JPA entity identification.
 * </p>
 */
public class SensorFaceId implements Serializable {

    private FaceDirection face;
    private long timestamp;

    /**
     * Default constructor for the SensorFaceId class.
     */
    public SensorFaceId() {}

    /**
     * Constructs a new SensorFaceId with the specified face direction and timestamp.
     *
     * @param face      the face direction of the sensor
     * @param timestamp the timestamp of the recorded data
     */
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