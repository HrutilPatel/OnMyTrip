package com.example.onmytrip.Persistence;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TransitAPI {

    private static final String API_KEY = "z5n7FCN-dAfzeP5JbMxU"; // Replace with your Winnipeg Transit API key

    public static void main(String[] args) {
        // Coordinates of the point to find locations near (latitude and longitude)
        double latitude = 49.86957;
        double longitude = -97.13718;
        int distance = 50; // Distance in metres from the given point

        // Build the API URL with parameters
        String apiUrl = buildApiUrl(latitude, longitude, distance);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get API response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response using Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

            // Process locations from the response
            JsonArray locations = jsonObject.getAsJsonArray("locations");
            System.out.println("Locations found:");

            for (int i = 0; i < locations.size(); i++) {
                JsonObject location = locations.get(i).getAsJsonObject();
                String name = location.get("name").getAsString();
                JsonObject address = location.getAsJsonObject("address");
                String streetName = address.getAsJsonObject("street").get("name").getAsString();
                System.out.println("- " + name + " at " + streetName);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildApiUrl(double latitude, double longitude, int distance) {
        // Base URL for the Winnipeg Transit API
        String baseUrl = "https://api.winnipegtransit.com/v3/locations.json";

        // Build query parameters
        Map<String, String> params = new HashMap<>();
        params.put("api-key", API_KEY);
        params.put("lat", String.valueOf(latitude));
        params.put("lon", String.valueOf(longitude));
        params.put("distance", String.valueOf(distance));
        params.put("usage", "long"); // or "short" for shorter names

        // Construct the full URL with query parameters
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove the last '&'

        return urlBuilder.toString();
    }
}
