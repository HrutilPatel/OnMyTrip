package com.example.onmytrip.ui.StopsPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StopsViewModel homeViewModel = new ViewModelProvider(this).get(StopsViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = root.findViewById(R.id.listView);
        stopsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(stopsAdapter);

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
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
