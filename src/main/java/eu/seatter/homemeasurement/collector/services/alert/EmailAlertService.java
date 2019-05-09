package eu.seatter.homemeasurement.collector.services.alert;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class EmailAlertService implements Alert {

    private final JavaMailSender mailSender;

    private Boolean alertEmailEnabled;

    public EmailAlertService(JavaMailSender mailSender, @Value("#{new Boolean('${message.alert.email.enabled:false}')}") Boolean alertEmailEnabled) {
        this.mailSender = mailSender;
        this.alertEmailEnabled = alertEmailEnabled;
    }

    @Override
    public void sendAlert(MailMessage mail) throws MessagingException {

        if(!alertEmailEnabled) {
            log.info("Email alerts disabled.");
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        String recipients;
        try {
            recipients = mail.getAddress();
        } catch (IllegalArgumentException ex) {
            throw new MessagingException("No email recipients defined");
        }

        String[] recipientList = recipients.split(";");

        for(String eID: recipientList) {
            helper.setTo(eID);
        }
        helper.setSubject(mail.getSubject());
        message.setContent(mail.getContent(), "text/html");

        log.debug("Email Recipients : " + recipients);
        log.debug("Email Subject : " + mail.getSubject());

        mailSender.send(message);
    }
}
