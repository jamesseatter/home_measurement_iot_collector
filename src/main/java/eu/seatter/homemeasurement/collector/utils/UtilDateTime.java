package eu.seatter.homemeasurement.collector.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 16/05/2020
 * Time: 16:51
 */
public class UtilDateTime {
    public static LocalDateTime getTimeDateNowNoSecondsInUTC() {
        LocalDateTime ldt = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
        return ldt;
    }
}
