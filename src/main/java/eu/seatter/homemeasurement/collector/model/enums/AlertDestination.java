package eu.seatter.homemeasurement.collector.model.enums;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 13/01/2020
 * Time: 08:29
 */
public enum AlertDestination {
    BORRY("AlertMeasurementEmailToBorryTemplate.html"),
    PRIVATE("AlertMeasurementEmailToPrivateTemplate.html");

    private final String alertTemplate;

    AlertDestination(String template) {
        this.alertTemplate = template;
    }

    public String getTemplate() {
        return this.alertTemplate;
    }

}
