package omc.sensormonitoring.repository;

import omc.sensormonitoring.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;



public interface SensorRepository extends JpaRepository<SensorData, Long> {

}
