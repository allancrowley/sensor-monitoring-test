package omc.sensorimitator;


import omc.sensorimitator.dto.SensorDataDto;
import omc.sensorimitator.service.SensorImitatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The main entry point for the Sensor Imitator application.
 * This class is responsible for initializing the Spring Boot application,
 * scheduling tasks, and sending sensor data to a specified endpoint.
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class SensorImitatorApplication {

	private final SensorImitatorService sensorImitatorService;
	private final RestTemplate restTemplate;

	@Value("${sensors.endpoint.host}")
	private String endpointHost;

	@Value("${sensors.endpoint.port}")
	private String endpointPort;

	@Value("${sensors.endpoint.path}")
	private String endpointPath;

	/**
	 * The main method that starts the Sensor Imitator application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(SensorImitatorApplication.class, args);
	}


	/**
	 * Scheduled method that generates random sensor data and sends it to the specified endpoint.
	 * This method is executed at a fixed rate as defined in the application properties.
	 * It logs the time taken for data generation and sending.
	 */
	@Scheduled(fixedRateString = "${sensors.sending.rate}")
	public void handleSensorData() {
		long startTime = System.currentTimeMillis();
		List<SensorDataDto> sensorDataList = sensorImitatorService.getRandomSensorData();
		long generationTime = System.currentTimeMillis() - startTime;
		sendSensorData(sensorDataList);
		long sendingTime = System.currentTimeMillis() - startTime - generationTime;
		log.info("Data generation took {} ms, sending took {} ms", generationTime, sendingTime);
	}


	/**
	 * Sends the generated sensor data to the specified endpoint asynchronously.
	 *
	 * This method takes a list of sensor data transfer objects (DTOs) and sends each DTO
	 * to the configured endpoint URL using a POST request. The sending is done asynchronously
	 * to improve performance and responsiveness. If sending fails for any DTO, an error is logged
	 * with the specific sensor ID that failed to send.
	 *
	 * @param sensorDataList a list of {@link SensorDataDto} objects representing the sensor data to be sent.
	 *                       Must not be null and can be empty.
	 */
	private void sendSensorData(List<SensorDataDto> sensorDataList) {
		String endpointUrl = String.format("%s:%s/%s", endpointHost, endpointPort, endpointPath);
		sensorDataList.forEach(sensorDataDto -> CompletableFuture.runAsync(() -> {
			try {
				restTemplate.postForEntity(endpointUrl, sensorDataDto, Void.class);
			} catch (Exception e) {
				log.error("Failed to send sensor data with id {}: {}", sensorDataDto.id(), e.getMessage());
			}
		}));
	}


}
