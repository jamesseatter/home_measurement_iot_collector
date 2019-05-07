package eu.seatter.homemeasurement.collector.services.alert;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/04/2019
 * Time: 13:24
 */
interface MailMessage {

    String getAddress();

    String getSubject();

    String getContent();
}
