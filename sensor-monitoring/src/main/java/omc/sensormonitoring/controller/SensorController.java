package omc.sensormonitoring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling sensor-related operations.
 * <p>
 * This controller provides endpoints for receiving and processing sensor data.
 * It acts as an intermediary between the client and the service layer,
 * facilitating the transfer of sensor data and responses.
 * </p>
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;

    /**
     * Receives sensor data from the client and saves it using the SensorService.
     *
     * @param sensorData the sensor data to be saved, provided as a JSON payload in the request body
     * @return a response entity indicating the status of the operation
     */
    @PostMapping("${sensors.path.input}")
    public ResponseEntity<String> receiveSensorData(@RequestBody @Valid SensorDataDto sensorData) {
        log.warn(sensorData.toString());
        sensorService.saveSensorData(sensorData);
        return ResponseEntity.ok("Sensor data received.");
    }
}