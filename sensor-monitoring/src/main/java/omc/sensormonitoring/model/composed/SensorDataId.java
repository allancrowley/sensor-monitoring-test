package omc.sensormonitoring.model.composed;

import omc.sensormonitoring.model.SensorData;

import java.io.Serializable;
import java.util.Objects;


public class SensorDataId implements Serializable {
    private Long id;
    private long timestamp;

    public SensorDataId() {}

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