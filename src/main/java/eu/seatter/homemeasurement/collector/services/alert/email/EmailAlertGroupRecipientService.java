package eu.seatter.homemeasurement.collector.services.alert.email;

import eu.seatter.homemeasurement.collector.model.AlertContactGroup;
import eu.seatter.homemeasurement.collector.services.alert.AlertContactJSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/04/2020
 * Time: 15:04
 */
@Service
@Slf4j
public class EmailAlertGroupRecipientService {
    public String getRecipients(String alertGroup) {
        if(alertGroup.isEmpty()) {
            return null;
        }
        AlertContactGroup ag = AlertContactJSON.getContactsForGroup(alertGroup).orElse(new AlertContactGroup());

        if(ag.getAddress().isEmpty()) {
            throw new java.lang.IllegalArgumentException("No recipients found for alertgroup:" + alertGroup);
        }
        log.debug("Email recipients : " + ag.getAddress());
        return (ag.getAddress());
    }
}
