package com.example.onmytrip.Persistence;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {

    private static final String HOURLY_FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast?q=Winnipeg,Manitoba&units=metric&appid=abc01e9fb6e500af8ad782d1984b211f";

    public interface WeatherDataListener {
        void onWeatherDataFetched(double temperature, double windSpeed);
        void onWeatherDataError(String errorMessage);
    }

    public static void fetchWeatherData(WeatherDataListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(HOURLY_FORECAST_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        return response.toString();
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    Log.e("WeatherAPI", "Error fetching weather data", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String jsonData) {
                if (jsonData != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        JSONArray hourlyData = obj.getJSONArray("list");
                        if (hourlyData.length() > 0) {
                            JSONObject firstHourData = hourlyData.getJSONObject(0);
                            JSONObject main = firstHourData.getJSONObject("main");
                            double temperature = main.getDouble("temp");
                            JSONObject wind = firstHourData.getJSONObject("wind");
                            double windSpeed = wind.getDouble("speed");
                            listener.onWeatherDataFetched(temperature, windSpeed);
                        } else {
                            listener.onWeatherDataError("No hourly forecast data available");
                        }
                    } catch (JSONException e) {
                        listener.onWeatherDataError("Error parsing JSON response: " + e.getMessage());
                    }
                } else {
                    listener.onWeatherDataError("Failed to fetch weather data");
                }
            }
        }.execute();
    }
}
