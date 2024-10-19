package omc.sensormonitoring.service;

import omc.sensormonitoring.model.SensorData;
import omc.sensormonitoring.repository.*;
import omc.sensormonitoring.util.DataUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceTests {

    @Mock
    SensorRepository sensorRepository;

    @Mock
    FaceAvgRepository faceAvgRepository;

    @Mock
    SensorDeviatedRepository sensorDeviatedRepository;

    @InjectMocks
    SensorServiceImpl serviceUnderTests;



    @Test
    @DisplayName("Test get average face direction temperatures by period functionality")
    public void givenStartOfPeriod_whenGetAvgFaceDirectionTemperatures_thenFindAllFromPeriodCalled() {
        //given
        long startOfPeriod = DataUtils.getStartOfPeriod();
        BDDMockito.when(faceAvgRepository.findAllFromPeriod(anyLong())).thenReturn(List.of());
        //when
        serviceUnderTests.getAvgFaceDirectionTemperatures(startOfPeriod);
        //then
        verify(faceAvgRepository, times(1)).findAllFromPeriod(startOfPeriod);
    }


    @Test
    @DisplayName("Test get malfunctioning sensors functionality")
    public void given_whenGetMalfunctioningSensors_thenFindAllCalled() {
        //given
        BDDMockito.when(sensorDeviatedRepository.findAll()).thenReturn(List.of());
        //when
        serviceUnderTests.getMalfunctioningSensors();
        //then
        verify(sensorDeviatedRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("Test calculate and store hourly average data functionality")
    public void given_whenCalculateAndStoreHourlyAverageData_thenRepositoriesAreCalled() {
        //given
        SensorData sensorDataToBeExtracted = DataUtils.getSensorData();
        BDDMockito.when(sensorRepository.findAggregatedSensorData(anyLong(), anyLong()))
                .thenReturn(List.of(sensorDataToBeExtracted));
        BDDMockito.when(sensorDeviatedRepository.saveAll(any(List.class))).thenReturn(List.of());
        BDDMockito.when(faceAvgRepository.saveAll(any(List.class))).thenReturn(List.of());
        BDDMockito.doNothing().when(sensorRepository).deleteSensorDataInRange(anyLong(), anyLong());
        //when
        serviceUnderTests.calculateAndStoreHourlyAverageData();
        //then
        verify(sensorRepository, times(1)).findAggregatedSensorData(anyLong(), anyLong());
        verify(sensorDeviatedRepository, times(1)).saveAll(any(List.class));
        verify(faceAvgRepository, times(1)).saveAll(any(List.class));
        verify(sensorRepository, times(1)).deleteSensorDataInRange(anyLong(), anyLong());
    }


}
