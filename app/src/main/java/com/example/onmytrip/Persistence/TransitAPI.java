package com.example.onmytrip.Persistence;

import android.os.AsyncTask;
import android.util.Log;

import com.example.onmytrip.Object.LongLat;
import com.example.onmytrip.Object.Stops;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransitAPI {

    private static LongLat location;
    private static ArrayList<Stops> stopsList;

    private static List<String> stepsList;
    private static int key ;

    public interface DataCallback {
        void onDataLoaded(String data);
        void onError(Exception e);
    }

    public interface TripPlanListener {
        void onTripPlanReady(List<String> steps);
        void onError(String errorMessage);
    }

    /*
    *
    *
    * FOR STOPS DATA
    *
    * */
    public static void getStopData(final DataCallback callback) {

        location = new LongLat();
        stopsList = new ArrayList<>();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL("https://api.winnipegtransit.com/v3/stops.json?api-key=z5n7FCN-dAfzeP5JbMxU&lat=49.8350&lon=-97.0562&distance=250");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Read the response
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    parseJasonStop(response);

                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;

            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null) {
                    callback.onDataLoaded(data);
                } else {
                    callback.onError(new IOException("Failed to fetch data"));
                }
            }
        }.execute();
    }

    /*
    *
    *
    * FOR LOCATION KEY
    *
    *
    * */
    public static void getLocationKey(double latitude, double longitude) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Construct the API URL

                    URL url = new URL("https://api.winnipegtransit.com/v3/locations.json?api-key=z5n7FCN-dAfzeP5JbMxU&lat=" + latitude + "&lon=" + longitude + "&distance=250");

                    // Open connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Read the response
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null) {
                    try {
                        // Parse JSON response
                        JSONObject jsonResponse = new JSONObject(data);
                        JSONArray locationsArray = jsonResponse.getJSONArray("locations");

                        if (locationsArray.length() > 0) {
                            // Get the first location from the array
                            JSONObject locationObject = locationsArray.getJSONObject(0);

                            // Get the address object within the location
                            JSONObject addressObject = locationObject.getJSONObject("address");

                            // Get the key from the address object
                            key = addressObject.getInt("key");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    /*
    *
    *
    * FOR TRIP PLANNING
    *
    *
    * */
    public static void getTripPlan(int originKey, int destinationKey) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {

                stepsList = new ArrayList<>();

                try {
                    // Construct the API URL
                    String apiKey = "z5n7FCN-dAfzeP5JbMxU"; // Replace with your API key
                    String apiUrl = "https://api.winnipegtransit.com/v3/trip-planner.json?api-key=" + apiKey
                            + "&origin=addresses/" + originKey
                            + "&destination=addresses/" + destinationKey;

                    // Create URL object and open connection
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Read response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray plansArray = jsonResponse.getJSONArray("plans");

                    // Iterate over trip plans
                    for (int i = 0; i < plansArray.length(); i++) {
                        JSONObject plan = plansArray.getJSONObject(i);
                        JSONArray segments = plan.getJSONArray("segments");

                        // Iterate over segments in the plan
                        for (int j = 0; j < segments.length(); j++) {
                            JSONObject segment = segments.getJSONObject(j);
                            String type = segment.getString("type");

                            // Add step to the list based on segment type
                            if (type.equals("walk")) {
                                JSONObject from = segment.getJSONObject("from");
                                JSONObject to = segment.getJSONObject("to");
                                String fromName = from.getJSONObject("stop").getString("name");
                                String toName = to.getJSONObject("destination").getJSONObject("monument").getString("name");
                                stepsList.add("Walk from " + fromName + " to " + toName);
                            } else if (type.equals("ride")) {
                                JSONObject route = segment.getJSONObject("route");
                                String routeName = route.getString("name");
                                String variantName = segment.getJSONObject("variant").getString("name");
                                stepsList.add("Ride " + routeName + " (" + variantName + ")");
                            }
                        }
                    }

                } catch (IOException | JSONException e) {
                    Log.e("TripPlanner", "Error fetching trip plan", e);
                }

                return stepsList;
            }

        }.execute();
    }

    public static int getKey(){
        return key;
    }

    public static void parseJasonStop(StringBuilder data) throws JSONException {
        stopsList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(data.toString());
        JSONArray stopsArray = jsonObject.getJSONArray("stops");

        for (int i = 0; i < stopsArray.length(); i++) {
            JSONObject stopObject = stopsArray.getJSONObject(i);
            String stopName = stopObject.getString("name");
            int stopNumber = stopObject.getInt("number");

            Stops stopData = new Stops();
            stopData.setName(stopName);
            stopData.setNumber(stopNumber);

            stopsList.add(stopData);
        }
    }

    public static ArrayList<Stops> getStopsList(){
        return stopsList;
    }

}
