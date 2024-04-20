package com.example.onmytrip.ui.TripPlanningPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.onmytrip.R;

import java.util.List;

public class TripPlanAdapter extends BaseAdapter {

    private Context context;
    private List<String> stepsList;

    public TripPlanAdapter(Context context, List<String> stepsList) {
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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.stepTextView = convertView.findViewById(R.id.stepTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String step = stepsList.get(position);
        viewHolder.stepTextView.setText(step);

        return convertView;
    }

    private static class ViewHolder {
        TextView stepTextView;
    }
}
