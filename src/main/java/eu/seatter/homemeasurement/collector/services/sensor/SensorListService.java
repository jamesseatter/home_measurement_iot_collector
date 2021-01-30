package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.Measurement;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 30/01/2021
 * Time: 14:52
 */
public interface SensorListService {
    List<Measurement> getSensors();
}
