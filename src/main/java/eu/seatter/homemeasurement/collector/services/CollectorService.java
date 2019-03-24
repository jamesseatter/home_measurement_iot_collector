package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
@Service
@Slf4j
public class CollectorService implements CommandLineRunner {
    private int readIntervalSeconds = 10;

    private SensorMeasurement sensorMeasurement;
    private SensorListService sensorListService;
    private DeviceService deviceService;

    public CollectorService(SensorMeasurement sensorMeasurement, SensorListService sensorListService, DeviceService deviceService) {
        this.sensorMeasurement = sensorMeasurement;
        this.sensorListService = sensorListService;
        this.deviceService = deviceService;
    }

    @Override
    public void run(String... strings) {
        boolean running = false;
//        try {
//            deviceService.registerDevice();
//        } catch (RuntimeException ex) {
//
//        }


        List<SensorRecord> sensorList = Collections.EMPTY_LIST;
        try {
            sensorList = sensorListService.getSensors();
            //todo Send sensor list to the Edge
        } catch (RuntimeException ex) {
            log.warn("No sensors were connected to the system. Shutting down");
            //throw new SensorNotFoundException("No sensors found",
//                    "Verify that sensors are connected to the device and try to restart the service. Verify the logs show the sensors being found.");
            running = false;
        }

        if(sensorList.size() > 0) {
            running = true;
        } else {
            log.info("No sensors connected to device. Exiting.");
        }

        while(running) {

            sensorMeasurement.collect(sensorList);

            try {
                Thread.sleep(readIntervalSeconds * 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                log.error(ex.getLocalizedMessage());
                running = false;
            }
        }
        log.info("Execution stopping");
    }
}
