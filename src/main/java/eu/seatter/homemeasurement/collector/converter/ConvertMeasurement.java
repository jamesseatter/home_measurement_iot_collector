package eu.seatter.homemeasurement.collector.converter;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementWeb;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 22/12/2022
 * Time: 08:48
 */
@Component
public class ConvertMeasurement {

    public List<MeasurementWeb> convertMeasurementToMeasurementWeb(@NotNull List<Measurement> measurement) {

        List<MeasurementWeb> measurementWeb = new ArrayList<>();
        for (Measurement m : measurement) {
            measurementWeb.add(convertMeasurementToMeasurementWeb(m));
        }
        return measurementWeb;
    }

    public Map<String,List<MeasurementWeb>> convertMeasurementToMeasurementWeb(@NotNull Map<String,List<Measurement>> measurement) {

        final Map<String,List<MeasurementWeb>> mweb = new LinkedHashMap<>();

        for(String id : measurement.keySet()) {
            mweb.put(id,convertMeasurementToMeasurementWeb(measurement.get(id)));
        }
        return mweb;
    }

    public MeasurementWeb convertMeasurementToMeasurementWeb(@NotNull Measurement m) {
        return MeasurementWeb.builder()
                .sensorid(m.getSensorid())
                .title(m.getTitle())
                .shorttitle(m.getShortTitle())
                .description(m.getDescription())
                .measureTimeUTC(m.getMeasureTimeUTC())
                .measurementUnit(m.getMeasurementUnit())
                .measurementadjustmentvalue(m.getMeasurementadjustmentvalue())
                .low_threshold(m.getLow_threshold())
                .high_threshold(m.getHigh_threshold())
                .value(m.getValue())
                .build();
    }
}
