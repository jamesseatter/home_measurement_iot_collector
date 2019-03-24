//package eu.seatter.homeheating.collector.services;
//
//import eu.seatter.homeheating.collector.model.SensorRecord;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 09/12/2018
// * Time: 15:25
// */
//@RunWith(MockitoJUnitRunner.class)
//public class IOTServiceImplTest {
//    @Mock
//    private SensorListService sensorListService;
//
//    @Mock
//    private SensorMeasurement sensorMeasurement;
//
//    @Mock
//    private DeviceService deviceService;
//
//    private CollectorService iotService;
//
//    private List<SensorRecord> sensorList = new ArrayList<>();
//    private SensorRecord mockData;
//
//
//    @Before
//    void setUp() {
//        iotService = new CollectorService(sensorMeasurement,sensorListService,deviceService);
//    }
//
//    @Test
//    void whenRegisterDevice_NoExceptions () {
//
//    }
//
//
//    @Test
//    void readSensorValue() {
//
//    }
//}