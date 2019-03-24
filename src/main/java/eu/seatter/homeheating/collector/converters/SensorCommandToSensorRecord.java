package eu.seatter.homeheating.collector.converters;

import eu.seatter.homeheating.collector.commands.SensorCommand;
import eu.seatter.homeheating.collector.model.SensorRecord;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/02/2019
 * Time: 23:05
 */
@Component
public class SensorCommandToSensorRecord {

    public SensorRecord convert(SensorCommand sensorRecord) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(sensorRecord, SensorRecord.class);
    }
}
