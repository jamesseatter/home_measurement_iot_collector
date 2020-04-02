package eu.seatter.homemeasurement.collector.sensor.listmanagers;

import eu.seatter.homemeasurement.collector.model.Measurement;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 12:34
 */
public interface SensorList {
    List<Measurement> getSensors();
}
