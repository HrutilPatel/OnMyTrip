package com.example.onmytrip.ui.StopsPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.onmytrip.Object.Stops;
import com.example.onmytrip.R;

import java.util.ArrayList;

public class StopsAdapter extends ArrayAdapter<Stops> {

    private ArrayList<Stops> stopsList;

    public StopsAdapter(Context context, ArrayList<Stops> stopsList) {
        super(context, 0, stopsList);
        this.stopsList = stopsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        Stops currentStop = stopsList.get(position);

        // Set the colored box color based on some condition (e.g., stop type)
        View coloredBox = listItemView.findViewById(R.id.coloredBox);

        // Set other views (e.g., name)
        TextView textViewName = listItemView.findViewById(R.id.textViewName);

        textViewName.setText("Stop Number : " + currentStop.getNumber() + "\n" +  currentStop.getName() );

        return listItemView;
    }
}
