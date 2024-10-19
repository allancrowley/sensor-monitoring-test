package omc.sensormonitoring.repository;

import omc.sensormonitoring.model.SensorFaceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaceAvgRepository extends JpaRepository<SensorFaceData, Long> {

}
