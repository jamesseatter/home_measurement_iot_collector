//package eu.seatter.homemeasurement.collector.database;
//
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 19/03/2020
// * Time: 22:11
// */
//@Setter
//@Getter
//@Entity
//@Table(name = "measurement")
//public class MeasurementEntity {
//    @Id
//    @GeneratedValue(strategy= GenerationType.AUTO)
//    private int id;
//    private UUID record_uid;
//    private LocalDateTime date_measured_utc;
//    private String sensor_type;
//    private String sensor_id;
//    private String title;
//    private String description;
//    private String measurement_unit;
//    private Double value;
//    private Double low_threshold;
//    private Double high_threshold;
//    @Builder.Default
//    private Boolean measurementsenttomq = false;
//
//    private String alert_uid;
//    private String alert_group;
//    private String alert_destination;
//    @Builder.Default
//    private Boolean alertsent_measurementtolerance = false;
//    @Builder.Default
//    private Boolean alertsent_failedmq = false;
//
//    protected MeasurementEntity() {
//        measurementsenttomq = false;
//        alertsent_measurementtolerance = false;
//        alertsent_failedmq = false;
//    }
//
//    public MeasurementEntity(UUID record_uid, LocalDateTime date_measured_utc, String sensor_type, String sensor_id, String title, String description, String measurement_unit, Double value, Double low_threshold, Double high_threshold, Boolean measurementsenttomq, String alert_uid, String alert_group, String alert_destination, Boolean alertSent_FailedMQ, Boolean alertSent_MeasurementTolerance) {
//        this.record_uid = record_uid;
//        this.date_measured_utc = date_measured_utc;
//        this.sensor_type = sensor_type;
//        this.sensor_id = sensor_id;
//        this.title = title;
//        this.description = description;
//        this.measurement_unit = measurement_unit;
//        this.value = value;
//        this.low_threshold = low_threshold;
//        this.high_threshold = high_threshold;
//        this.measurementsenttomq = measurementsenttomq;
//        this.alert_uid = alert_uid;
//        this.alert_group = alert_group;
//        this.alert_destination = alert_destination;
//        this.alertsent_failedmq = alertSent_FailedMQ;
//        this.alertsent_measurementtolerance = alertSent_MeasurementTolerance;
//
//    }
//
//    @Override
//    public String toString() {
//        return String.format(
//                "Measurement[recordid=%d, measurementDateUTC='%s', sensorId='%s', value='%s' '%s']",
//                record_uid, sensor_id, measurement_unit);
//    }
//}
