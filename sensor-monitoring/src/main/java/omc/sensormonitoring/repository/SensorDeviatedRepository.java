package omc.sensormonitoring.repository;

import omc.sensormonitoring.model.SensorDeviatedData;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for managing {@link SensorDeviatedData} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations for sensor
 * deviated data entries, allowing for easy storage and retrieval of records
 * that indicate sensor malfunctions or deviations from expected temperature ranges.
 * </p>
 */
public interface SensorDeviatedRepository extends JpaRepository<SensorDeviatedData, Long> {
    // No additional methods are defined in this repository at present.

}
