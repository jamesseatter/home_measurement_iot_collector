package eu.seatter.homemeasurement.collector.services.alert.email;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/04/2019
 * Time: 13:24
 */
interface EMailMessage {

    String getAddress();

    String getSubject();

    String getContent();
}
