package eu.seatter.homemeasurement.collector.model;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:21
 */
public enum SensorMeasurementUnit {
    C ("C"),
    F ("F");

    private final String displayName;

    SensorMeasurementUnit(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { return displayName; }

    // Optionally and/or additionally, toString.
    @Override public String toString() { return displayName; }
}
