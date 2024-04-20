package com.example.onmytrip.ui.TripPlanningPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        ListView listView = view.findViewById(R.id.listView2);

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

    private void processAddress(String originAddress, String destinationAddress){

        int originKey = 0;
        int destinationKey = 0;

        location.getLatLongFromAddress(originAddress, getContext());
        transitApi.getLocationKey(location.getRqLatitude(), location.getRqLongitude());
        originKey = transitApi.getKey();

        location.getLatLongFromAddress(destinationAddress, getContext());
        transitApi.getLocationKey(location.getRqLatitude(), location.getRqLongitude());
        destinationKey = transitApi.getKey();

    }

}
