package eu.seatter.homemeasurement.collector.model;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 02/05/2019
 * Time: 11:38
 */

public class AlertContactGroup {
    private String name;
    private String type;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
