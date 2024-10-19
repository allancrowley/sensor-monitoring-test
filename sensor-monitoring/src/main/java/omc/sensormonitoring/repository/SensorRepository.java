package omc.sensormonitoring.repository;

import jakarta.transaction.Transactional;
import omc.sensormonitoring.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SensorRepository extends JpaRepository<SensorData, Long> {

    @Query("SELECT new SensorData(s.id, MAX(s.timestamp), s.face, ROUND(AVG(s.temperature), 2)) " +
            "FROM SensorData s " +
            "WHERE s.timestamp BETWEEN :startTimestamp AND :endTimestamp " +
            "GROUP BY s.id, s.face")
    List<SensorData> findAggregatedSensorData(@Param("startTimestamp") long startTimestamp,
                                              @Param("endTimestamp") long endTimestamp);


    @Modifying
    @Transactional
    @Query("DELETE FROM SensorData s WHERE s.timestamp BETWEEN :startTimestamp AND :endTimestamp")
    void deleteSensorDataInRange(@Param("startTimestamp") long startTimestamp,
                                 @Param("endTimestamp") long endTimestamp);

}
