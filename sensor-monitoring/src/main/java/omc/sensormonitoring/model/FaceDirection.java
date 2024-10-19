package omc.sensormonitoring.model;

/**
 * Enum representing the possible directions that a sensor can face.
 *
 * <p>Each sensor can be oriented in one of four cardinal directions:</p>
 * <ul>
 *   <li>{@link #SOUTH}</li>
 *   <li>{@link #EAST}</li>
 *   <li>{@link #NORTH}</li>
 *   <li>{@link #WEST}</li>
 * </ul>
 */
public enum FaceDirection {
    /**
     * Represents the South direction.
     */
    SOUTH,

    /**
     * Represents the East direction.
     */
    EAST,

    /**
     * Represents the North direction.
     */
    NORTH,

    /**
     * Represents the West direction.
     */
    WEST
}