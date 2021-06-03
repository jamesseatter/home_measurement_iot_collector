package eu.seatter.homemeasurement.collector.cache.map;

import eu.seatter.homemeasurement.collector.model.SystemAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 04/02/2021
 * Time: 23:44
 */
class AlertSystemCacheMapImplTest {

    private String alertTitle;
    private String alertMessage;
    private final int cacheMaxSize = 24;

    private final AlertSystemCacheMapImpl cache = new AlertSystemCacheMapImpl("test","test",cacheMaxSize);

    @BeforeEach
    public void setUp() {
        alertTitle =  "Azure connection";
        alertMessage = "Failed to connect to Azure IOT Hub";
    }

    @Test
    void whenAddThreeAlerts_thenReturnCountThree() {
        //given
        cache.add(alertTitle,alertMessage);
        cache.add(alertTitle,alertMessage);
        cache.add(alertTitle,alertMessage);
        //when

        //then
        assertEquals(3, cache.getAll().size());
    }

    @Test
    void whenAddAlert_thenReturnValidAlertSystemObject() {
        //given
        SystemAlert sa = cache.add(alertTitle,alertMessage);
        //when

        //then
        assertEquals(sa, cache.getAll().get(0));
    }

    @Test
    void WhenCacheSize_ThenReturnSameSize() {
        assertEquals(cacheMaxSize,cache.getCacheMaxSize());
    }

    @Test
    void WhenAdd2Alerts_ThenCacheSizeIs2() {
        cache.add(alertTitle,alertMessage);
        cache.add(alertTitle,alertMessage);

        assertEquals(2, cache.getCacheSize());
    }
}