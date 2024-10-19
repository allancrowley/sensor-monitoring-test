package omc.sensormonitoring.repository;

import omc.sensormonitoring.model.SensorFaceData;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repository interface for managing {@link SensorFaceData} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations for sensor face
 * average temperature data. It includes methods for retrieving average temperature
 * records based on specified time periods.
 * </p>
 */
public interface FaceAvgRepository extends JpaRepository<SensorFaceData, Long> {

    /**
     * Retrieves a list of {@link SensorFaceData} entities that have a timestamp
     * greater than or equal to the specified timestamp.
     *
     * @param timestamp the starting timestamp for retrieving sensor face data
     * @return a list of {@link SensorFaceData} entities
     */
    @Query("SELECT s FROM SensorFaceData s WHERE s.timestamp >= :timestamp")
    List<SensorFaceData> findAllFromPeriod(@Param("timestamp") long timestamp);





}
