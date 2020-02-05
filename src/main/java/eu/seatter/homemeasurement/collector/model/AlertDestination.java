package eu.seatter.homemeasurement.collector.model;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 13/01/2020
 * Time: 08:29
 */
public enum AlertDestination {
    BORRY("AlertMeasurementEmailToBorryTemplate.html"),
    PRIVATE("AlertMeasurementEmailToPrivateTemplate.html");

    private String alert_template;

    AlertDestination(String template) {
        this.alert_template = template;
    }

    public String getTemplate() {
        return this.alert_template;
    }

}
