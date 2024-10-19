package omc.sensormonitoring.controller.handler;

/**
 * This interface defines a set of error message constants used throughout
 * the sensor monitoring application. These messages are utilized to provide
 * user-friendly feedback in case of various errors related to sensor data
 * processing and API interactions.
 */
public interface ErrorMessages {
    /** Error message for type mismatch in URL parameters. */
    String TYPE_MISMATCH_MESSAGE = "URL parameter has type mismatch";

    /** Error message for type mismatch in JSON fields. */
    String JSON_TYPE_MISMATCH_MESSAGE = "JSON contains field with type mismatch";

    /** Error message indicating that the requested resource does not exist. */
    String NO_RESOURCE_FOUND_MESSAGE = "Requested page does not exists";

    /** Error message indicating that the sensor ID is missing. */
    String MISSING_SENSOR_ID_MESSAGE = "Sensor id is missing";

    /** Error message indicating that the temperature value is missing for a sensor. */
    String MISSING_SENSOR_TEMPERATURE_MESSAGE = "Temperature is missing";

    /** Error message indicating that the face direction for the sensor is missing. */
    String MISSING_SENSOR_FACE_DIRECTION_MESSAGE = "Face direction is missing";

    /** Error message indicating that the timestamp for the sensor data is missing. */
    String MISSING_SENSOR_TIMESTAMP_MESSAGE = "Timestamp is missing";
}
