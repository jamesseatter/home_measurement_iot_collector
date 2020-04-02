//package eu.seatter.homemeasurement.collector.cache.database;
//
//import eu.seatter.homemeasurement.collector.cache.MeasurementCache;
//import eu.seatter.homemeasurement.collector.database.MeasurementEntity;
//import eu.seatter.homemeasurement.collector.database.MeasurementRepository;
//import eu.seatter.homemeasurement.collector.model.Measurement;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 01/05/2019
// * Time: 16:02
// */
//@Slf4j
//@Component
//@Scope("singleton")
//public class MeasurementCacheDBImpl implements MeasurementCache {
//    private final MeasurementRepository measurementRepository;
//
//    @Value("${measurement.cache.max_records_per_sensor:24}")
//    private final int MAX_ENTRIES_PER_SENSOR=24;
//
//    public MeasurementCacheDBImpl(MeasurementRepository measurementRepository) {
//        this.measurementRepository = measurementRepository;
//    }
//
//    @Override
//    public void add(Measurement measurement) {
//        Measurement toCache = measurement.toBuilder().build();
//        System.out.println(measurement.hashCode() + "   /   " + toCache.hashCode());
//        if(measurement == null) {
//            throw new IllegalArgumentException("The content of measurement is null");
//        }
//        MeasurementEntity me = new MeasurementEntity(
//                measurement.getRecordUID(),
//                measurement.getMeasureTimeUTC().toLocalDateTime(),
//                measurement.getSensorType().toString(),
//                measurement.getSensorid(),
//                measurement.getTitle(),
//                measurement.getDescription(),
//                measurement.getMeasurementUnit().toString(),
//                measurement.getValue(),
//                measurement.getLow_threshold(),
//                measurement.getHigh_threshold(),
//                measurement.getMeasurementSentToMq(),
//                measurement.getAlertId(),
//                measurement.getAlertgroup(),
//                measurement.getAlertdestination(),
//                measurement.getAlertSentToMQ(),
//                measurement.getAlertSent_MeasurementTolerance()
//        );
//
//        measurementRepository.save(me);
//        //TODO remove old entries UNLESS they are not in MQ already
////        if(measurementRepository.getCountOfSensorRecords(measurement.getSensorid()) > MAX_ENTRIES_PER_SENSOR) {
////
////        }
//
//        log.debug("Alert cache Add : " + toCache.toString());
//    }
//
//    public void measurementSentToMq(UUID recorduid, Boolean status) {
//        measurementRepository.update_MeasurementSentToMq(recorduid,status);
//    }
//
//    public void alertSentToMq(UUID recorduid, Boolean status) {
//        measurementRepository.update_alertSent_FailedMQ(recorduid,status);
//    }
//
//    public Measurement getMeasurementById(UUID recordId) {
//        return measurementRepository.getByRecordId(recordId);
//    }
//
//    @Override
//    public Map<String, List<Measurement>> getAll() {
//        //Iterable<MeasurementEntity> measurements = measurementRepository.findAll();
//        Map<String, List<Measurement>> measurementsMap = new HashMap<>();
//
//        return measurementsMap;
//    }
//
//    @Override
//    public Map<String, List<Measurement>> getAllSorted() {
//        return null;
//    }
//
//    @Override
//    public List<Measurement> getAllBySensorId(String sensorId) {
//        return null;
//    }
//
//    @Override
//    public List<Measurement> getLastBySensorId(String sensorId, int last) {
//        return null;
//    }
//
//    @Override
//    public ArrayList<String> getSensorIds() {
//        return null;
//    }
//
//    @Override
//    public int getCacheMaxSizePerSensor() {
//        return 0;
//    }
//
//    @Override
//    public int getCacheSizeBySensorId(String sensorId) {
//        return 0;
//    }
//}
