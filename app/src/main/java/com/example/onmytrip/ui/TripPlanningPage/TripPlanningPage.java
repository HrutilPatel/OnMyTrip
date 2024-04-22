package com.example.onmytrip.ui.TripPlanningPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.onmytrip.R;

import java.util.List;

public class TripPlanningPage extends BaseAdapter {

    private Context context;
    private List<String> stepsList;

    public TripPlanningPage(Context context, List<String> stepsList) {
        this.context = context;
        this.stepsList = stepsList;
    }

    @Override
    public int getCount() {
        return stepsList.size();
    }

    @Override
    public Object getItem(int position) {
        return stepsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trip_step_item, parent, false);
        }

        // Get the step string at the current position
        String step = stepsList.get(position);

        // Bind step data to views
        TextView stepTextView = convertView.findViewById(R.id.stepTextView);
        stepTextView.setText(step);

        return convertView;
    }
}
