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

    private final SensorDeviatedRepository sensorDeviatedRepository;
    private final FaceAvgRepository faceAvgRepository;
    private final SensorRepository sensorRepository;

    private final JdbcTemplate jdbcTemplate;

    @Value("${sensors.db.batch.size}")
    private int BATCH_SIZE;
    @Value("${sensors.db.batch.frequency}")
    private int BATCH_SAVE_FREQUENCY;
    @Value("${sensors.temperature.acceptable.deviation}")
    private double DEVIATION_PERCENTAGE;


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


    public List<SensorFaceData> getAvgFaceDirectionTemperatures(long startOfPeriod) {
        return faceAvgRepository.findAllFromPeriod(startOfPeriod);
    }



    public List<SensorDeviatedData> getMalfunctioningSensors() {
        return sensorDeviatedRepository.findAll();
    }



    @Scheduled(cron = "${sensors.scheduling.cron}")
    @Transactional
    public void calculateAndStoreHourlyAverageData() {
        try {
            long currentRoundHour = getRoundHourInMillis(System.currentTimeMillis());
            long previousRoundHour = currentRoundHour - HOUR_IN_MILLIS;
            log.info("Extracting data for period: {} - {} at {}",
                    convertMillisToLocalTime(previousRoundHour),
                    convertMillisToLocalTime(currentRoundHour),
                    convertMillisToLocalTime(System.currentTimeMillis()));

            List<SensorData> avgBySensor = sensorRepository.findAggregatedSensorData(previousRoundHour, currentRoundHour);
            if (avgBySensor.isEmpty()) {
                log.warn("No sensor data found");
                return;
            }
            log.debug("Extracted average temperature by sensor for the last hour at: {}",
                    convertMillisToLocalTime(System.currentTimeMillis()));
            processAndSaveSensorData(avgBySensor, currentRoundHour);
        } catch (DataAccessException e) {
            log.error("Error handling sensors data: {}", e.getMessage());
        }
    }


    private void processAndSaveSensorData(List<SensorData> avgBySensor, long currentRoundHour) {
        Map<FaceDirection, Double> avgByDirection = calculateFaceAvgTemperature(avgBySensor);
        List<SensorDeviatedData> deviatedSensors = calculateDeviatedSensors(avgByDirection, avgBySensor);
        sensorDeviatedRepository.saveAll(deviatedSensors);
        List<SensorFaceData> sensorFaceList = calculateFaceDirection(avgByDirection, currentRoundHour);
        faceAvgRepository.saveAll(sensorFaceList);
        deleteOldSensorData(currentRoundHour - HOUR_IN_MILLIS, currentRoundHour);
    }



    private void deleteOldSensorData(long previousRoundHour, long currentRoundHour) {
        sensorRepository.deleteSensorDataInRange(previousRoundHour, currentRoundHour);
        log.debug("Removed hourly data from DB at: {}",
                convertMillisToLocalTime(System.currentTimeMillis()));
    }



    private List<SensorDeviatedData> calculateDeviatedSensors(Map<FaceDirection, Double> faceAvgTemperature, List<SensorData> avgData) {
        long curHour = getRoundHourInMillis(System.currentTimeMillis());

        return avgData.stream()
                .filter(sensor -> {
                    double avgTemperature = Optional.ofNullable(faceAvgTemperature.get(sensor.getFace()))
                            .orElse(0.0);
                    double maxDeviation = avgTemperature * DEVIATION_PERCENTAGE;
                    return Math.abs(sensor.getTemperature() - avgTemperature) > maxDeviation;
                })
                .map(d -> new SensorDeviatedData(d.getId(), curHour, d.getFace(), d.getTemperature()))
                .peek(d -> log.error("Deviation detected for sensor ID {} at {}", d.getId(), d.getTemperature()))
                .toList();
    }



    private Map<FaceDirection, Double> calculateFaceAvgTemperature(List<SensorData> avgData) {

        return avgData.stream()
                .collect(Collectors.groupingBy(
                        SensorData::getFace,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(SensorData::getTemperature),
                                avg -> Math.round(avg * 100.0) / 100.0
                        )
                ));
    }



    private List<SensorFaceData> calculateFaceDirection(Map<FaceDirection, Double> avgByDirection, long currentHour) {

        return avgByDirection.entrySet().stream()
                .map(e -> new SensorFaceData(currentHour, e.getKey(), e.getValue()))
                .toList();
    }



    private static long getRoundHourInMillis(long currentTime) {

        return currentTime - (currentTime % HOUR_IN_MILLIS);
    }



    private static LocalTime convertMillisToLocalTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);

        return instant.atZone(ZoneId.systemDefault()).toLocalTime();
    }




}