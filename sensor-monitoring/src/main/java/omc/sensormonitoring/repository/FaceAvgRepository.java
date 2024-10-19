package omc.sensormonitoring.repository;

import omc.sensormonitoring.model.SensorFaceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FaceAvgRepository extends JpaRepository<SensorFaceData, Long> {

    @Query("SELECT s FROM SensorFaceData s WHERE s.timestamp >= :timestamp")
    List<SensorFaceData> findAllFromPeriod(@Param("timestamp") long timestamp);

}
