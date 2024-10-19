package omc.sensormonitoring.repository;

import jakarta.transaction.Transactional;
import omc.sensormonitoring.model.SensorData;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repository interface for managing {@link SensorData} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations for sensor data and includes
 * custom query methods for aggregating and deleting sensor data within a specified timestamp range.
 * </p>
 */
public interface SensorRepository extends JpaRepository<SensorData, Long> {

    /**
     * Retrieves aggregated sensor data within the specified timestamp range.
     * <p>
     * This method calculates the maximum timestamp and the average temperature for each sensor,
     * grouping the results by sensor ID and face direction.
     * </p>
     *
     * @param startTimestamp the start of the timestamp range (in milliseconds since epoch)
     * @param endTimestamp   the end of the timestamp range (in milliseconds since epoch)
     * @return a list of {@link SensorData} objects containing the aggregated results
     */
    @Query("SELECT new SensorData(s.id, MAX(s.timestamp), s.face, ROUND(AVG(s.temperature), 2)) " +
            "FROM SensorData s " +
            "WHERE s.timestamp BETWEEN :startTimestamp AND :endTimestamp " +
            "GROUP BY s.id, s.face")
    List<SensorData> findAggregatedSensorData(@Param("startTimestamp") long startTimestamp,
                                              @Param("endTimestamp") long endTimestamp);


    /**
     * Deletes sensor data within the specified timestamp range.
     * <p>
     * This method performs a batch delete operation for sensor data entries whose timestamps
     * fall within the given range.
     * </p>
     *
     * @param startTimestamp the start of the timestamp range (in milliseconds since epoch)
     * @param endTimestamp   the end of the timestamp range (in milliseconds since epoch)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SensorData s WHERE s.timestamp BETWEEN :startTimestamp AND :endTimestamp")
    void deleteSensorDataInRange(@Param("startTimestamp") long startTimestamp,
                                 @Param("endTimestamp") long endTimestamp);


}
