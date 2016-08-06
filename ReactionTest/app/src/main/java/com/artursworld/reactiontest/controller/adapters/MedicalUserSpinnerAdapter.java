package com.artursworld.reactiontest.controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;

/*
* Displays user and its attributes in a spinner
*/
public class MedicalUserSpinnerAdapter extends ArrayAdapter<String> {

    private int[] images = {};
    private String[] midicalIds = {};
    private int[] ages = {};
    private Context context;
    private LayoutInflater inflater;

    public MedicalUserSpinnerAdapter(Context context, String[] medicalIds, int[] ages, int[] genderImages) {
        super(context, R.layout.medical_user_spinner_adapter, medicalIds);
        this.context = context;
        this.midicalIds = medicalIds;
        this.ages = ages;
        this.images = genderImages;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    /*
    * Holds the attributes too display in a spinner cell
    */
    public class ViewHolder {
        TextView medicalId;
        TextView age;
        ImageView gender;
    }

    /*
    * Returns the view to display user and its attributes in a spinner
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.medical_user_spinner_adapter, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.medicalId = (TextView) convertView.findViewById(R.id.medical_user_spinner_adapter_medical_id);
        holder.age = (TextView) convertView.findViewById(R.id.medical_user_spinner_adapter_age);
        holder.gender = (ImageView) convertView.findViewById(R.id.medical_user_spinner_adapter_image);

        holder.gender.setImageResource(images[position]);
        holder.medicalId.setText(context.getResources().getString(R.string.id) + ": " + midicalIds[position]);
        holder.age.setText(context.getResources().getString(R.string.age) + ": " + ages[position]);

        return convertView;
    }
}
