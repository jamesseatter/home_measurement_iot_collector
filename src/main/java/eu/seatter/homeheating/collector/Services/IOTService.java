package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.SensorRecord;
import eu.seatter.homeheating.collector.Sensor.SensorListManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
@Service
@Slf4j
public class IOTService implements CommandLineRunner {
    private int readIntervalSeconds = 10;

    private SensorMeasurement sensorMeasurement;
    private SensorListManager sensorListManager;

    public IOTService(SensorMeasurement sensorMeasurement, SensorListManager sensorListManager) {
        this.sensorMeasurement = sensorMeasurement;
        this.sensorListManager = sensorListManager;
    }

    @Override
    public void run(String... strings) {
        List<SensorRecord> sensorList = new ArrayList<>();
        boolean running = false;
        try {
            sensorList = sensorListManager.getSensors();
            log.info("Loaded list of sensors. Count : " + sensorList.size());
            //todo Send sensor list to the Edge
        } catch (RuntimeException ex) {
            //todo add new Exception, SensorsNotFoundException.
        }

        if(sensorList.size() > 0) {
            running = true;
        }

        while(running) {

            sensorMeasurement.collect(sensorList);

            try {
                Thread.sleep(readIntervalSeconds * 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        log.info("Execution stopping");
    }


}
