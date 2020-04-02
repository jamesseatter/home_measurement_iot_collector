//package eu.seatter.homemeasurement.collector.database;
//
//import eu.seatter.homemeasurement.collector.model.Measurement;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.query.Param;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 19/03/2020
// * Time: 22:38
// */
//
//public interface MeasurementRepository extends CrudRepository<MeasurementEntity, Long> {
//    @Transactional
//    @Modifying
//    @Query("UPDATE MeasurementEntity m SET m.measurementsenttomq = :status WHERE m.record_uid = :recordUID")
//    int update_MeasurementSentToMq(@Param("recordUID") UUID recordUID, @Param("status") Boolean status);
//
//    @Transactional
//    @Modifying
//    @Query("UPDATE MeasurementEntity m SET m.alertsent_failedmq = :status WHERE m.record_uid = :recordUID")
//    int update_alertSent_FailedMQ(@Param("recordUID") UUID recordUID, @Param("status") Boolean status);
//
//    @Transactional
//    @Modifying
//    @Query("UPDATE MeasurementEntity m SET m.alertsent_measurementtolerance = :status WHERE m.record_uid = :recordUID")
//    int update_alertSent_MeasurementTolerance(@Param("recordUID") UUID recordUID, @Param("status") Boolean status);
//
//    @Query("Select m FROM MeasurementEntity m WHERE m.record_uid = :recordUID")
//    Measurement getByRecordId(@Param("recordUID") UUID recordUID);
//
//    @Query("select count(m) From MeasurementEntity m where m.sensor_id = :sensorid")
//    int getCountOfSensorRecords(@Param("sensorid") String sensorID);
//}
