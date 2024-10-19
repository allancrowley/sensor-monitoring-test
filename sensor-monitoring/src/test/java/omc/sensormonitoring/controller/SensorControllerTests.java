package omc.sensormonitoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import omc.sensormonitoring.controller.handler.ErrorMessages;
import omc.sensormonitoring.dto.SensorDataDto;
import omc.sensormonitoring.service.SensorService;
import omc.sensormonitoring.util.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
public class SensorControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private SensorService serviceUnderTests;


    @Test
    @DisplayName("Test save correct sensor data functionality")
    public void givenCorrectSensorDataDto_whenReceiveSensorData_thenSensorDataSaved() throws Exception {
        //given
        SensorDataDto sensorDataToBeSent = DataUtils.getCorrectSensorData();
        BDDMockito.doNothing().when(serviceUnderTests).saveSensorData(any(SensorDataDto.class));
        //when
        ResultActions result = mockMvc.perform(post("/sensors/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDataToBeSent)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Sensor data received."));
    }

    @Test
    @DisplayName("Test save sensor data without id functionality")
    public void givenSensorDataDtoWithoutId_whenReceiveSensorData_thenExceptionThrown() throws Exception {
        //given
        SensorDataDto sensorDataToBeSent = DataUtils.getSensorDataMissingId();
        //when
        ResultActions result = mockMvc.perform(post("/sensors/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDataToBeSent)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(ErrorMessages.MISSING_SENSOR_ID_MESSAGE));
    }

    @Test
    @DisplayName("Test save sensor data without timestamp functionality")
    public void givenSensorDataDtoWithoutTimestamp_whenReceiveSensorData_thenExceptionThrown() throws Exception {
        //given
        SensorDataDto sensorDataToBeSent = DataUtils.getSensorDataMissingTimestamp();
        //when
        ResultActions result = mockMvc.perform(post("/sensors/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDataToBeSent)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(ErrorMessages.MISSING_SENSOR_TIMESTAMP_MESSAGE));
    }

    @Test
    @DisplayName("Test save sensor data without direction functionality")
    public void givenSensorDataDtoWithoutDirection_whenReceiveSensorData_thenExceptionThrown() throws Exception {
        //given
        SensorDataDto sensorDataToBeSent = DataUtils.getSensorDataMissingDirection();
        //when
        ResultActions result = mockMvc.perform(post("/sensors/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDataToBeSent)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(ErrorMessages.MISSING_SENSOR_FACE_DIRECTION_MESSAGE));
    }

    @Test
    @DisplayName("Test save sensor data without temperature functionality")
    public void givenSensorDataDtoWithoutTemperature_whenReceiveSensorData_thenExceptionThrown() throws Exception {
        //given
        SensorDataDto sensorDataToBeSent = DataUtils.getSensorDataMissingTemperature();
        //when
        ResultActions result = mockMvc.perform(post("/sensors/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDataToBeSent)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(ErrorMessages.MISSING_SENSOR_TEMPERATURE_MESSAGE));
    }
}
