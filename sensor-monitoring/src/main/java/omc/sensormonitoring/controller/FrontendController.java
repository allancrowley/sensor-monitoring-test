package omc.sensormonitoring.controller;

import lombok.RequiredArgsConstructor;
import omc.sensormonitoring.model.SensorDeviatedData;
import omc.sensormonitoring.model.SensorFaceData;
import omc.sensormonitoring.service.SensorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * REST controller for handling frontend-related operations related to sensor data.
 * <p>
 * This controller provides endpoints to retrieve and display sensor data,
 * specifically average temperatures and malfunctioning sensors, to the frontend.
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class FrontendController {
    private final long WEEK_IN_MILLIS = 60 * 60 * 1000 * 24 * 7;
    private final SensorService sensorService;

    /**
     * Retrieves average face temperatures and malfunctioning sensors,
     * adds them to the model, and returns the name of the view to render.
     *
     * @param model the model to which attributes are added for rendering the view
     * @return the name of the view to be rendered (in this case, "sensorData")
     */
    @GetMapping("${sensors.path.output}")
    public String getSensorData(Model model) {
        long timestampBeforeWeek = System.currentTimeMillis() - WEEK_IN_MILLIS;
        List<SensorFaceData> avgFaceTemperatures = sensorService.getAvgFaceDirectionTemperatures(timestampBeforeWeek);
        List<SensorDeviatedData> deviatedSensors = sensorService.getMalfunctioningSensors();
        model.addAttribute("avgFaceTemperatures", avgFaceTemperatures);
        model.addAttribute("deviatedSensors", deviatedSensors);
        return "sensorData";
    }
}
