package com.artursworld.reactiontest.controller.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.model.entity.Medicament;

import java.util.List;

/**
 * Adapter for medicament's to display in a view
 */
public class MedicamentListAdapter extends ArrayAdapter<String> {

    private List<Medicament> medicamentList = null;
    private Activity activity = null;

    public MedicamentListAdapter(Activity activity, List<Medicament> medicamentList) {
        super(activity.getApplicationContext(), R.layout.adapter_medicament_list);
        this.activity = activity;
        this.medicamentList = medicamentList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = activity.getApplicationContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_medicament_list, null);
        }

        TextView timeLabel = (TextView) convertView.findViewById(R.id.ad_time_label);
        TextView dateLabel = (TextView) convertView.findViewById(R.id.ad_date_label);
        TextView medicamentNameLabel = (TextView) convertView.findViewById(R.id.ad_name_label);
        TextView dosisLabel = (TextView) convertView.findViewById(R.id.ad_dosis_label);
        TextView unitLabel = (TextView) convertView.findViewById(R.id.ad_unit_label);

        if(timeLabel != null)
            timeLabel.setText(medicamentList.get(position).getTime());

        if(dateLabel != null)
            dateLabel.setText(medicamentList.get(position).getDate());

        if(medicamentNameLabel != null)
            medicamentNameLabel.setText(medicamentList.get(position).getName());

        if(dosisLabel != null)
            dosisLabel.setText(medicamentList.get(position).getDosage()+"");

        if(unitLabel != null)
            unitLabel.setText(medicamentList.get(position).getUnit());

        return convertView;
    }

}
