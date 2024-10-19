package omc.sensormonitoring.repository;

import omc.sensormonitoring.model.SensorDeviatedData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDeviatedRepository extends JpaRepository<SensorDeviatedData, Long> {
}
