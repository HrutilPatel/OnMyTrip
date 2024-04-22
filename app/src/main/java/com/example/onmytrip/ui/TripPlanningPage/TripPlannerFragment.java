package com.example.onmytrip.ui.TripPlanningPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onmytrip.Object.LongLat;
import com.example.onmytrip.Persistence.TransitAPI;
import com.example.onmytrip.R;
import com.example.onmytrip.ui.StopsPage.StopsAdapter;

import java.util.ArrayList;
import java.util.List;

public class TripPlannerFragment extends Fragment {

    private EditText originEditText;
    private EditText destinationEditText;
    private TransitAPI transitApi;

    private StopsAdapter adapter;
    private List<String> stepsList;
    private ListView listView;

    private LongLat location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        originEditText = view.findViewById(R.id.editTextText);
        destinationEditText = view.findViewById(R.id.editTextText2);
        listView = view.findViewById(R.id.listView2);

        transitApi = new TransitAPI();
        stepsList = new ArrayList<>();
        location = new LongLat();

        Button searchButton = view.findViewById(R.id.button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originAddress = originEditText.getText().toString();
                String destinationAddress = destinationEditText.getText().toString();

                if (!originAddress.isEmpty() && !destinationAddress.isEmpty()) {
                    processAddress(originAddress, destinationAddress);
                } else {
                    Toast.makeText(requireContext(), "Please enter both origin and destination", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processAddress(String originAddress, String destinationAddress) {
        // Retrieve latitude and longitude for origin address
        location.getLatLongFromAddress(originAddress, getContext());
        double originLatitude = location.getRqLatitude();
        double originLongitude = location.getRqLongitude();

        // Retrieve latitude and longitude for destination address
        location.getLatLongFromAddress(destinationAddress, getContext());
        double destinationLatitude = location.getRqLatitude();
        double destinationLongitude = location.getRqLongitude();

        transitApi.originAddress = originAddress;
        transitApi.destinationAddress = destinationAddress;

        // Use callbacks or a listener approach to handle the asynchronous response
        transitApi.getLocationKey(originLatitude, originLongitude, new TransitAPI.KeyCallback() {
            @Override
            public void onKeyReceived(int originKey) {
                // Origin key retrieved, now fetch destination key
                transitApi.getLocationKey(destinationLatitude, destinationLongitude, new TransitAPI.KeyCallback() {
                    @Override
                    public void onKeyReceived(int destinationKey) {
                        // Both origin and destination keys retrieved, proceed with trip planning
                        transitApi.getTripPlan(originKey, destinationKey, new TransitAPI.TripPlanListener() {
                            @Override
                            public void onTripPlanReady(List<String> steps) {
                                // Display trip steps in ListView
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, steps);
                                listView.setAdapter(adapter);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("DestinationKeyError", errorMessage);
                        // Handle error retrieving destination key
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("OriginKeyError", errorMessage);
                // Handle error retrieving origin key
            }
        });
    }



}
