package eu.seatter.homeheating.collector.model;

import lombok.Getter;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 22:45
 */
@Getter
public enum DeviceIdentification {
    INSTANCE();

    String hostName;
    String macAddress;
    String os;
    String manufacturer;

    DeviceIdentification() {
        try {
            this.hostName = InetAddress.getLocalHost().getHostName();
            this.macAddress = macaddress();
            this.os = System.getProperty("os.name");
            this.manufacturer = "Pi";
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    String macaddress(){
        StringBuilder sb = new StringBuilder();
        try {
            InetAddress addr = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(addr);
            byte[] mac = network.getHardwareAddress();

            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
        } catch (Exception ex) {
            //todo improve exception handling
        }
        return sb.toString();
    }

    public DeviceIdentification getInstance() {
        return INSTANCE;
    }
}
