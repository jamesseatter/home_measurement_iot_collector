package eu.seatter.homemeasurement.collector.converters;

import eu.seatter.homemeasurement.collector.commands.SensorCommand;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/02/2019
 * Time: 23:00
 */
@Component
public class SensorRecordToSensorCommand {

    public SensorCommand convert(SensorRecord sensorRecord) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(sensorRecord, SensorCommand.class);
    }
}
