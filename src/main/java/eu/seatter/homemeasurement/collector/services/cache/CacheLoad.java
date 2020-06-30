package eu.seatter.homemeasurement.collector.services.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 30/06/2020
 * Time: 11:17
 */
@Service
@Slf4j
public class CacheLoad {
    private final MeasurementCacheService measurementCacheService;
    private final MQMeasurementCacheService mqMeasurementCacheService;
    private final AlertMeasurementCacheService alertMeasurementCacheService;

    public CacheLoad(
            MeasurementCacheService measurementCacheService,
            MQMeasurementCacheService mqMeasurementCacheService,
            AlertMeasurementCacheService alertMeasurementCacheService) {
        this.measurementCacheService = measurementCacheService;
        this.mqMeasurementCacheService = mqMeasurementCacheService;
        this.alertMeasurementCacheService = alertMeasurementCacheService;
    }

    public void load() {
        try {
            log.info("Load mq cache");
            int count = mqMeasurementCacheService.readFromFile();
            log.info("Loaded " + count + " mq records");
        } catch (Exception ex) {
            log.error("Error loading cached mq entries from file. No cached data will be loaded : " + ex.getMessage());
        }
        try {
            log.info("Load measurement cache");
            int count = measurementCacheService.readFromFile();
            log.info("Loaded " + count + " measurement records");
        } catch (Exception ex) {
            log.error("Error loading cached measurement entries from file. No cached data will be loaded : " + ex.getMessage());
        }
        try {
            log.info("Load system alert cache");
            int count = alertMeasurementCacheService.readFromFile();
            log.info("Loaded " + count + " alert records");
        } catch (Exception ex) {
            log.error("Error loading cached system alert entries from file. No cached data will be loaded : " + ex.getMessage());
        }
    }
}
