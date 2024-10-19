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


	public static void main(String[] args) {
		SpringApplication.run(SensorImitatorApplication.class, args);
	}


	@Scheduled(fixedRateString = "${sensors.sending.rate}")
	public void handleSensorData() {
		long startTime = System.currentTimeMillis();
		List<SensorDataDto> sensorDataList = sensorImitatorService.getRandomSensorData();
		long generationTime = System.currentTimeMillis() - startTime;
		sendSensorData(sensorDataList);
		long sendingTime = System.currentTimeMillis() - startTime - generationTime;
		log.info("Data generation took {} ms, sending took {} ms", generationTime, sendingTime);
	}


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
