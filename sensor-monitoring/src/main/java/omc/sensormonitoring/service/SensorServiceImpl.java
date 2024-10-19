package omc.sensormonitoring.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.model.FaceDirection;
import omc.sensormonitoring.model.SensorData;
import omc.sensormonitoring.model.SensorDeviatedData;
import omc.sensormonitoring.model.SensorFaceData;
import omc.sensormonitoring.repository.FaceAvgRepository;
import omc.sensormonitoring.repository.SensorDeviatedRepository;
import omc.sensormonitoring.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SensorServiceImpl implements SensorService {
    private final ConcurrentLinkedQueue<SensorDataDto> sensorQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService batchExecutor = Executors.newScheduledThreadPool(5);
    private final JdbcTemplate jdbcTemplate;

    @Value("${sensors.db.batch.size}")
    private int BATCH_SIZE;
    @Value("${sensors.db.batch.frequency}")
    private int BATCH_SAVE_FREQUENCY;


    private static final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    private static final String INSERT_SENSOR_DATA_QUERY =
            "INSERT INTO sensor_data (id, timestamp, face, temperature) VALUES (?, ?, ?, ?)";


    @PostConstruct
    public void initializeExecutor() {
        batchExecutor.scheduleAtFixedRate(this::flushQueue, 0, BATCH_SAVE_FREQUENCY, TimeUnit.SECONDS);
    }


    public void saveSensorData(SensorDataDto sensorDataDto) {
        sensorQueue.add(sensorDataDto);
    }

    @Override
    public List<SensorFaceData> getAvgFaceDirectionTemperatures(long startOfPeriod) {
        return List.of();
    }

    @Override
    public List<SensorDeviatedData> getMalfunctioningSensors() {
        return List.of();
    }


    void flushQueue() {
        if (sensorQueue.isEmpty()) {
            log.trace("No sensor data to flush.");
            return;
        }

        List<SensorDataDto> batchList = new ArrayList<>();
        while (!sensorQueue.isEmpty() && batchList.size() < BATCH_SIZE) {
            batchList.add(sensorQueue.poll());
        }

        if (!batchList.isEmpty()) {
            try {
                saveSensorDataInBatch(batchList);
            } catch (DataAccessException e) {
                log.error("Error saving sensor data batch: {}", e.getMessage());
            }
        }
    }


    private void saveSensorDataInBatch(List<SensorDataDto> sensorDataDtoList) {
        List<Object[]> batchArgs = sensorDataDtoList.stream()
                .map(sensor -> new Object[]{sensor.id(), sensor.timestamp(), sensor.face().name(), sensor.temperature()})
                .toList();

        jdbcTemplate.batchUpdate(INSERT_SENSOR_DATA_QUERY, batchArgs);
        log.debug("Saved into DB batch with size {}", batchArgs.size());
    }




}