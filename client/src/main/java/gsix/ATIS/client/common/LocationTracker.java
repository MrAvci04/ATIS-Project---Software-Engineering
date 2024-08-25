package gsix.ATIS.client.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.StdCallLibrary;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONException;



public class LocationTracker {

    // Interface definition for the Windows Location API
    public interface LocationApi extends StdCallLibrary {
        LocationApi INSTANCE = Native.load("LocationAPI", LocationApi.class);

        int GetCurrentLocation(int flags, LocationInfo location);
    }

    // Structure definition for location information
    public static class LocationInfo extends Structure {
        public double latitude;
        public double longitude;

        public LocationInfo() {
            super();
        }

        public LocationInfo(Pointer pointer) {
            super(pointer);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("latitude", "longitude");
        }
    }

    public static String getLocation() {
        try {
            // URL for geolocation API
            URL url = new URL("https://ipapi.co/json/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(response.toString());

            // Extract latitude and longitude as double values
            double latitude = jsonObject.getDouble("latitude");
            double longitude = jsonObject.getDouble("longitude");

            // Construct string with latitude and longitude
            String location = "Latitude: " + latitude + ", Longitude: " + longitude;

            return location;

        } catch (IOException e) {
            e.printStackTrace();
            return "Location not available";
        }
    }
    /*  GET DEVICE IP ADDRESS. TESTED AND WORKING
    public static String getLocation() {
        try {
            // URL for geolocation API (example using ipapi.com)
            URL url = new URL("https://api.ipify.org/?format=json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Return the response
            System.out.println("In getLocation fun.\nLocation is: "+response.toString());
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Location not available";
        }
    }

     */


    // Method to retrieve the device's location using the Windows Location API
    /*public static String getLocation() {
        try {
            LocationInfo locationInfo = new LocationInfo();
            int result = LocationApi.INSTANCE.GetCurrentLocation(0, locationInfo);
            if (result == 0) {
                return "Location: Latitude " + locationInfo.latitude + ", Longitude " + locationInfo.longitude;
            } else {
                return "Location not available";
            }
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            return "Location API not available";
        }
    }*/
        /*try {
            // URL for geolocation API (example using ipapi.com)
            URL url = new URL("https://api.ipify.org/?format=json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Return the response
            System.out.println("In getLocation fun.\nLocation is: "+response.toString());
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Location not available";
        }*/

}
