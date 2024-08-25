package gsix.ATIS.client.common;
import java.net.*;
public class DeviceIdentifier {


    // Method to retrieve the MAC address of the device
    public static String getMACAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(localHost);
            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
