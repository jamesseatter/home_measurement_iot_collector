package eu.seatter.homemeasurement.collector.services.alert;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/04/2019
 * Time: 13:13
 */
public interface  Alert {
    void sendAlert(MailMessage mail) throws MessagingException;
}
