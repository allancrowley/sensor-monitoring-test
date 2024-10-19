package omc.sensormonitoring.model.composed;

import omc.sensormonitoring.model.SensorData;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the composite primary key for the {@link SensorData} entity.
 * <p>
 * This class encapsulates the unique identifiers for a sensor's data, combining
 * a unique sensor ID and the corresponding timestamp of the temperature reading.
 * It implements {@link Serializable} to allow instances of this class to be serialized,
 * which is required for JPA entity identification.
 * </p>
 */
public class SensorDataId implements Serializable {
    private Long id;
    private long timestamp;

    /**
     * Default constructor for the SensorDataId class.
     */
    public SensorDataId() {}

    /**
     * Constructs a new SensorDataId with the specified sensor ID and timestamp.
     *
     * @param id        the unique identifier for the sensor
     * @param timestamp the timestamp of the recorded data
     */
    public SensorDataId(Long id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorDataId)) return false;
        SensorDataId that = (SensorDataId) o;
        return timestamp == that.timestamp && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp);
    }
}