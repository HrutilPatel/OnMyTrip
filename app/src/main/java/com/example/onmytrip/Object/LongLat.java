package com.example.onmytrip.Object;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import java.util.List;
import java.util.Locale;

public class LongLat {

    private double rqLatitude;
    private double rqLongitude;

    public double getUserLongitude() throws IOException {
        Map<String, Double> location = getIPGeolocation();
        if (location != null && location.containsKey("lon")) {
            return location.get("lon");
        }
        return 0; // Default value if longitude is not found
    }

    public double getUserLatitude() throws IOException {
        Map<String, Double> location = getIPGeolocation();
        if (location != null && location.containsKey("lat")) {
            return location.get("lat");
        }
        return 0; // Default value if latitude is not found
    }



    private static Map<String, Double> getIPGeolocation() throws IOException {
        String ipAddress = getPublicIPAddress();
        if (ipAddress == null) {
            return null;
        }

        String apiUrl = "http://ip-api.com/json/" + ipAddress;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            Map<String, Double> location = new HashMap<>();
            Gson gson = new Gson();
            Map<String, Object> jsonResponse = gson.fromJson(response.toString(), Map.class);

            if (jsonResponse.containsKey("lat") && jsonResponse.containsKey("lon")) {
                location.put("lat", Double.valueOf(String.valueOf(jsonResponse.get("lat"))));
                location.put("lon", Double.valueOf(String.valueOf(jsonResponse.get("lon"))));
                return location;
            }
        }

        return null;
    }

    private static String getPublicIPAddress() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String ipAddress = reader.readLine().trim();
        reader.close();
        return ipAddress;
    }

    public double getRqLatitude(){
        return rqLatitude;
    }

    public double getRqLongitude(){
        return rqLongitude;
    }

    public void getLatLongFromAddress(String address, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(address+",Winnipeg,Manitoba", 1);
            if (addresses != null && addresses.size() > 0) {

                Address location = addresses.get(0);
                rqLatitude = location.getLatitude();
                rqLongitude = location.getLongitude();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
