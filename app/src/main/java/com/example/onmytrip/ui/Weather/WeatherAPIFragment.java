package com.example.onmytrip.ui.Weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.onmytrip.Persistence.WeatherAPI;
import com.example.onmytrip.R;

public class WeatherAPIFragment extends Fragment {
    private TextView temperatureTextView;
    private TextView windchillsTextView;

    private WeatherAPI weather;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_header_main, container, false);

        weather = new WeatherAPI();

        // Get references to the TextView elements
        temperatureTextView = view.findViewById(R.id.textView);
        windchillsTextView = view.findViewById(R.id.textView3);

        temperatureTextView.setText(String.valueOf(weather.getTemperature()));
        windchillsTextView.setText(String.valueOf(weather.getWindSpeed()));

        return view;
    }
}
