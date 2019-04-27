package eu.seatter.homemeasurement.collector.services.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/04/2019
 * Time: 13:14
 */
@Service
public class EmailAlertService implements Alert {

    private final JavaMailSender mailSender;

    @Value("${alert.mail.recipient}")
    private String recipients;

    public EmailAlertService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendAlert(MailMessage mail) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        String[] recipientList = recipients.split(";");

        for(String eID: recipientList) {
            helper.setTo(eID);
        }
        helper.setSubject(mail.getSubject());
        message.setContent(mail.getContent(), "text/html");

        mailSender.send(message);
    }
}
