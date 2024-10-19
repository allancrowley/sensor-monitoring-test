package omc.sensormonitoring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;


    @PostMapping("${sensors.path.input}")
    public ResponseEntity<String> receiveSensorData(@RequestBody SensorDataDto sensorData) {
        sensorService.saveSensorData(sensorData);
        return ResponseEntity.ok("Sensor data received.");
    }
}