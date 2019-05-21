package eu.seatter.homemeasurement.collector.services.alert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seatter.homemeasurement.collector.model.AlertContactGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
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

    private static String configPath;

    public AlertContactJSON(@Value("${config.path}") String configpath) {
        configPath = configpath;
    }

    public static Optional<AlertContactGroup> GetContactsForGroup(String groupName){
        log.info("Start JSON Alert Contacts import");
        File alertGroupFileLocation;

        alertGroupFileLocation = new File(configPath,"alertcontacts.json");
        log.info("Alert Contacts File Location " + alertGroupFileLocation.toString());

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
