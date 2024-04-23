package com.example.onmytrip.Persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONException;
public class WeatherAPI {
    private static final String WINNIPEG_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=Winnipeg,ca&units=metric&appid=abc01e9fb6e500af8ad782d1984b211f";
    private static final String HOURLY_FORECAST_URL = "http://api.openweathermap.org/data/2.5/onecall?exclude=current,minutely,daily,alerts&lat=49.8844&lon=-97.147&appid=abc01e9fb6e500af8ad782d1984b211f" ;

    private static double temperature ;
    private static double windSpeed;


    public static void getHourlyForecast() {
        try {
            URL url = new URL(HOURLY_FORECAST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            parseWeatherData(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseWeatherData(String jsonData) {
        try {
            JSONObject obj = new JSONObject(jsonData);

            // Get weather condition for the next hour
            String weatherCondition = obj.getJSONArray("hourly").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");

            // Get temperature for the next hour
            temperature = obj.getJSONArray("hourly").getJSONObject(0).getDouble("temp");


            // Get wind speed for the next hour
            windSpeed = obj.getJSONArray("hourly").getJSONObject(0).getDouble("wind_speed");

        } catch (JSONException e) {
            System.out.println("An error occurred while parsing the JSON data: " + e.getMessage());
        }
    }
    public static void getWinnipegWeather() {
        try {
            URL url = new URL(WINNIPEG_WEATHER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getTemperature(){
        return temperature;
    }

    public double getWindSpeed(){
        return windSpeed;
    }

}

