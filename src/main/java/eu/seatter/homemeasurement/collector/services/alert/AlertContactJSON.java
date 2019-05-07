package eu.seatter.homemeasurement.collector.services.alert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seatter.homemeasurement.collector.CollectorApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 02/05/2019
 * Time: 11:19
 */
@Component
@Slf4j
public class AlertContactJSON {

    public static Optional<AlertContactGroup> GetContactsForGroup(String groupName) {
        File alertGroupFileLocation;

        try {
            URI path = CollectorApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            alertGroupFileLocation = new File(new File(path).getParent(), "/config/alertcontacts.json");
            log.info("Alert Contacts File Location " + alertGroupFileLocation.toString());
        } catch (URISyntaxException ex) {
            log.info("Unable to find application location : " + ex.getLocalizedMessage());
            return Optional.empty();
        }

        if (!(alertGroupFileLocation.exists())) {
            log.info("Alert Group file does not exist at location. Import terminated");
            return Optional.empty();
        }
        log.info("Loading alert groups");

        List<AlertContactGroup> alertContactGroups;
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        TypeReference<List<AlertContactGroup>> typeReference = new TypeReference<List<AlertContactGroup>>() {
        };

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(alertGroupFileLocation);
        } catch (
                FileNotFoundException ex) {
            log.info("Alert Group File not found at location");
            return Optional.empty();
        }

        try {
            alertContactGroups = mapper.readValue(inputStream, typeReference);

        } catch (
                IOException ex) {
            log.error("Unable to read in JSON file: " + ex.getMessage());
            return Optional.empty();
        }

        return alertContactGroups.stream().filter(sr -> sr.getName().equals(groupName.trim())).findFirst();
    }

}
