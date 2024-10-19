package omc.sensormonitoring.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.model.*;
import omc.sensormonitoring.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link SensorService} interface that manages sensor data processing and storage.
 * <p>
 * This service is responsible for receiving sensor data, calculating hourly averages, detecting sensor deviations,
 * and flushing batched data to the database. It uses a concurrent queue for handling incoming sensor data
 * and executes scheduled tasks for data processing.
 * </p>
 * <p>
 * The service integrates with several repositories to perform database operations and applies business rules
 * such as acceptable temperature deviations for sensor data.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SensorServiceImpl implements SensorService {
    private final ConcurrentLinkedQueue<SensorDataDto> sensorQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService batchExecutor = Executors.newScheduledThreadPool(5);
    private final SensorRepository sensorRepository;
    private final SensorDeviatedRepository sensorDeviatedRepository;
    private final FaceAvgRepository faceAvgRepository;
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


    /**
     * Initializes the scheduled executor for flushing the sensor data queue.
     */
    @PostConstruct
    public void initializeExecutor() {
        batchExecutor.scheduleAtFixedRate(this::flushQueue, 0, BATCH_SAVE_FREQUENCY, TimeUnit.SECONDS);
    }


    /**
     * Saves sensor data into the concurrent queue.
     *
     * @param sensorDataDto the sensor data to save
     */
    public void saveSensorData(SensorDataDto sensorDataDto) {
        sensorQueue.add(sensorDataDto);
    }


    /**
     * Flushes the sensor data queue and saves data in batches to the database.
     */
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


    /**
     * Saves a list of sensor data in batch to the database.
     *
     * @param sensorDataDtoList the list of sensor data to save
     */
    private void saveSensorDataInBatch(List<SensorDataDto> sensorDataDtoList) {
        List<Object[]> batchArgs = sensorDataDtoList.stream()
                .map(sensor -> new Object[]{sensor.id(), sensor.timestamp(), sensor.face().name(), sensor.temperature()})
                .toList();

        jdbcTemplate.batchUpdate(INSERT_SENSOR_DATA_QUERY, batchArgs);
        log.debug("Saved into DB batch with size {}", batchArgs.size());
    }


    /**
     * Retrieves average face direction temperatures for a given period.
     *
     * @param startOfPeriod the start time of the period
     * @return a list of average face direction temperatures
     */
    public List<SensorFaceData> getAvgFaceDirectionTemperatures(long startOfPeriod) {
        return faceAvgRepository.findAllFromPeriod(startOfPeriod);
    }


    /**
     * Retrieves a list of malfunctioning sensors.
     *
     * @return a list of malfunctioning sensor data
     */
    public List<SensorDeviatedData> getMalfunctioningSensors() {
        return sensorDeviatedRepository.findAll();
    }


    /**
     * Scheduled task that calculates and stores hourly average sensor data.
     */
    @Override
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


    /**
     * Processes and saves sensor data, including calculating average temperatures
     * and detecting deviated sensors.
     *
     * @param avgBySensor    the list of average sensor data
     * @param currentRoundHour the current rounded hour in milliseconds
     */
    private void processAndSaveSensorData(List<SensorData> avgBySensor, long currentRoundHour) {
        Map<FaceDirection, Double> avgByDirection = calculateFaceAvgTemperature(avgBySensor);
        List<SensorDeviatedData> deviatedSensors = calculateDeviatedSensors(avgByDirection, avgBySensor);
        sensorDeviatedRepository.saveAll(deviatedSensors);
        List<SensorFaceData> sensorFaceList = calculateFaceDirection(avgByDirection, currentRoundHour);
        faceAvgRepository.saveAll(sensorFaceList);
        deleteOldSensorData(currentRoundHour - HOUR_IN_MILLIS, currentRoundHour);
    }


    /**
     * Deletes old sensor data from the database within the specified time range.
     *
     * @param previousRoundHour the start of the time range
     * @param currentRoundHour  the end of the time range
     */
    private void deleteOldSensorData(long previousRoundHour, long currentRoundHour) {
        sensorRepository.deleteSensorDataInRange(previousRoundHour, currentRoundHour);
        log.debug("Removed hourly data from DB at: {}",
                convertMillisToLocalTime(System.currentTimeMillis()));
    }



    /**
     * Calculates the list of deviated sensors based on average temperatures.
     *
     * @param faceAvgTemperature a map of average face temperatures
     * @param avgData           the list of average sensor data
     * @return a list of deviated sensor data
     */
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
                .peek(d -> log.error("Deviation detected for sensor ID {} at {}", d.getId(), convertMillisToLocalTime(d.getTimestamp())))
                .toList();
    }


    /**
     * Calculates the average temperature for each face direction.
     *
     * @param avgData the list of sensor data to calculate averages from
     * @return a map of average temperatures by face direction
     */
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


    /**
     * Converts average face temperatures into a list of SensorFaceData objects.
     *
     * @param avgByDirection a map of average temperatures by face direction
     * @param currentHour    the current rounded hour in milliseconds
     * @return a list of SensorFaceData objects
     */
    private List<SensorFaceData> calculateFaceDirection(Map<FaceDirection, Double> avgByDirection, long currentHour) {
        return avgByDirection.entrySet().stream()
                .map(e -> new SensorFaceData(currentHour, e.getKey(), e.getValue()))
                .toList();
    }


    /**
     * Rounds the current time in milliseconds to the nearest hour.
     *
     * @param currentTime the current time in milliseconds
     * @return the rounded hour in milliseconds
     */
    private static long getRoundHourInMillis(long currentTime) {
        return currentTime - (currentTime % HOUR_IN_MILLIS);
    }


    /**
     * Converts milliseconds to LocalTime.
     *
     * @param millis the time in milliseconds
     * @return the corresponding LocalTime
     */
    private static LocalTime convertMillisToLocalTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        return instant.atZone(ZoneId.systemDefault()).toLocalTime();
    }

}