//package eu.seatter.homemeasurement.collector.services;
//
//import eu.seatter.homemeasurement.collector.commands.RegistrationCommand;
//import eu.seatter.homemeasurement.collector.model.Device;
//import eu.seatter.homemeasurement.collector.model.DeviceIdentification;
//import eu.seatter.homemeasurement.collector.model.RegistrationStatus;
//import eu.seatter.homemeasurement.collector.services.device.DeviceServiceImpl;
//import eu.seatter.homemeasurement.collector.services.encryption.EncryptionService;
//import eu.seatter.homemeasurement.collector.services.encryption.EncyptionServiceASE;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.*;
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 04/02/2019
// * Time: 13:05
// */
//@RunWith(MockitoJUnitRunner.class)
//public class DeviceServiceImplTest {
//
//    @Mock
//    private RegistrationService restClientServiceMock;
//    @Mock
//    private EncryptionService encryptionServiceMock;
//
//    private DeviceIdentification di;
//
//    private DeviceServiceImpl deviceService;
//
//    private final RegistrationCommand returnedRegistrationCommand = new RegistrationCommand();
//
//    private String uniqueId;
//
//    @Before
//    public void setUp() {
//        //MockitoAnnotations.initMocks(this);
//        EncryptionService encryptionService2 = new EncyptionServiceASE();
//        String macAddress = "9C-B6-D0-F9-85-34";
//        uniqueId = encryptionService2.encryptString(macAddress);
//
//        returnedRegistrationCommand.setId(1L);
//        returnedRegistrationCommand.setName("TESTDevice");
//        returnedRegistrationCommand.setManufacturer("Pi");
//        returnedRegistrationCommand.setOperatingSystem("Raspberian");
//        returnedRegistrationCommand.setRegistrationStatus(RegistrationStatus.APPROVED);
//        returnedRegistrationCommand.setUniqueId(uniqueId);
//
//        deviceService = new DeviceServiceImpl(restClientServiceMock,encryptionServiceMock);
//    }
//
//    @Test
//    public void whenDeviceExists_ShouldReturnDeviceAPPROVED() {
//        //given
//        when(restClientServiceMock.isCollectorRegistered(anyString())).thenReturn(returnedRegistrationCommand);
//        when(encryptionServiceMock.encryptString(anyString())).thenReturn(uniqueId);
//
//        //when
//        RegistrationCommand res = deviceService.registerDevice();
//
//        //then
//        assertEquals(res.getUniqueId(),uniqueId);
//        assertEquals(res.getRegistrationStatus(),RegistrationStatus.APPROVED);
//        verify(restClientServiceMock).isCollectorRegistered(anyString());
//        verify(restClientServiceMock, never()).registerCollector(any(Device.class));
//    }
//
//    @Test
//    public void whenDeviceNotRegistered_ShouldReturnDevicePendingApproval() {
//        //given
//        returnedRegistrationCommand.setRegistrationStatus(RegistrationStatus.PENDINGAPPROVAL);
//        when(restClientServiceMock.isCollectorRegistered(anyString())).thenReturn(new RegistrationCommand());
//        when(restClientServiceMock.registerCollector(any(Device.class))).thenReturn(returnedRegistrationCommand);
//        when(encryptionServiceMock.encryptString(anyString())).thenReturn(uniqueId);
//
//        //when
//        RegistrationCommand res = deviceService.registerDevice();
//
//        //then
//        assertEquals(res.getUniqueId(),uniqueId);
//        assertEquals(res.getRegistrationStatus(),RegistrationStatus.PENDINGAPPROVAL);
//        verify(restClientServiceMock).isCollectorRegistered(anyString());
//        verify(restClientServiceMock).registerCollector(any(Device.class));
//    }
//}
