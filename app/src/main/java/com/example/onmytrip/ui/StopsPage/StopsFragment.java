package com.example.onmytrip.ui.StopsPage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.onmytrip.Object.Stops;
import com.example.onmytrip.Persistence.TransitAPI;
import com.example.onmytrip.R;
import com.example.onmytrip.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class StopsFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayAdapter<Stops> stopsAdapter;
    private double longitude;
    private double latitude;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StopsViewModel homeViewModel = new ViewModelProvider(this).get(StopsViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = root.findViewById(R.id.listView);
        stopsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(stopsAdapter);

        // Getting user's location
        getLocation();

        TransitAPI.getStopData(new TransitAPI.DataCallback() {
            @Override
            public void onDataLoaded(String data) {
                try {
                    TransitAPI.parseJasonStop(new StringBuilder(data));
                    ArrayList<Stops> stopsList = TransitAPI.getStopsList();
                    if (stopsList != null) {

                        stopsAdapter = new StopsAdapter(requireContext(), stopsList);
                        listView.setAdapter(stopsAdapter);
                        stopsAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        }, longitude, latitude);

        return root;
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            // Check if GPS is enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Request location updates
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                }, null);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
