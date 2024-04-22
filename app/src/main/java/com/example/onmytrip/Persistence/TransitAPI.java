package com.example.onmytrip.Persistence;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransitAPI {

    private static LongLat location;
    public String originAddress;
    public String destinationAddress;
    private static ArrayList<Stops> stopsList;

    private static List<String> stepsList;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface DataCallback {
        void onDataLoaded(String data);
        void onError(Exception e);
    }

    public interface TripPlanListener {
        void onTripPlanReady(List<String> steps);
        void onError(String errorMessage);
    }

    public interface KeyCallback {
        void onKeyReceived(int key);
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
    public static void getLocationKey(double latitude, double longitude, final KeyCallback callback) {
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

                        // Get the array of locations
                        JSONArray locationsArray = jsonResponse.getJSONArray("locations");

                        if (locationsArray.length() > 0) {
                            // Get the first location from the array
                            JSONObject firstLocation = locationsArray.getJSONObject(0);

                            // Check if the location has an address object
                            if (firstLocation.has("address")) {
                                // Get the address object within the location
                                JSONObject addressObject = firstLocation.getJSONObject("address");

                                // Get the key from the address object
                                int key = addressObject.getInt("key");

                                // Trigger callback with the obtained key
                                callback.onKeyReceived(key);

                            } else {
                                callback.onError("No address found for the first location");
                            }
                        } else {
                            callback.onError("No locations found in the response");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Error parsing JSON: " + e.getMessage());
                    }
                } else {
                    callback.onError("Empty response");
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
    public void getTripPlan(int originKey, int destinationKey, TripPlanListener listener) {
        executorService.execute(() -> {
            List<String> stepsList = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                String apiUrl = "https://api.winnipegtransit.com/v3/trip-planner.json?api-key=z5n7FCN-dAfzeP5JbMxU"
                        + "&origin=addresses/" + originKey
                        + "&destination=addresses/" + destinationKey;

                URL url = new URL(apiUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Read response
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

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
                            String fromName = getLocationName(from);
                            JSONObject to = segment.getJSONObject("to");
                            String toName = getLocationName(to);
                            stepsList.add("Walk from " + fromName + " to " + toName);
                        } else if (type.equals("ride")) {
                            JSONObject route = segment.getJSONObject("route");
                            String routeName = route.getString("name");
                            String variantName = segment.getJSONObject("variant").getString("name");
                            stepsList.add("Ride " + routeName + " (" + variantName + ")");
                        }
                    }
                }

                // Notify listener on the main thread
                mainHandler.post(() -> listener.onTripPlanReady(stepsList));

            } catch (IOException | JSONException e) {
                // Notify listener of error on the main thread
                mainHandler.post(() -> listener.onError("Error fetching trip plan: " + e.getMessage()));

            } finally {
                // Clean up resources
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("TransitAPI", "Error closing reader", e);
                    }
                }
            }
        });
    }

    // Helper method to get location name from a JSON object (either from or to)
    private String getLocationName(JSONObject locationObject) throws JSONException {
        if (locationObject.has("stop")) {
            JSONObject stop = locationObject.getJSONObject("stop");
            return stop.getString("name");
        } else if (locationObject.has("destination")) {
            JSONObject destination = locationObject.getJSONObject("destination");
            if (destination.has("monument")) {
                JSONObject monument = destination.getJSONObject("monument");
                return monument.getString("name");
            } else if (destination.has("address")) {
                JSONObject address = destination.getJSONObject("address");
                // Assuming address contains street information
                String streetName = address.getJSONObject("street").getString("name");
                String streetType = address.getJSONObject("street").getString("type");
                return streetName + " " + streetType;
            }
        }
        return originAddress;
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
