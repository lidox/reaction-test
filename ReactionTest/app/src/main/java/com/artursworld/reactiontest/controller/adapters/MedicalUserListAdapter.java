package com.artursworld.reactiontest.controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;

/**
 * Displays a user and its attributes in a cell
 */
public class MedicalUserListAdapter extends ArrayAdapter<String> {

    private int[] images = {};
    private String[] midicalIds = {};
    private String[] ages = {};
    private Context context;
    private LayoutInflater inflater;

    public MedicalUserListAdapter(Context context, String[] medicalIds, String[] birthdates, int[] genderImages){
        super(context, R.layout.medical_user_list_adapter, medicalIds);
        this.context = context;
        this.midicalIds = medicalIds;
        this.ages = birthdates;
        this.images = genderImages;
    }

    /*
    * Displays attributes in a cell
    */
    public class ViewHolder{
        TextView medicalId;
        TextView age;
        ImageView gender;
    }

    /*
    * Returns the view and its user content
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.medical_user_list_adapter, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.medicalId = (TextView) convertView.findViewById(R.id.medical_user_list_adapter_medical_id);
        holder.age = (TextView) convertView.findViewById(R.id.medical_user_list_adapter_age);
        holder.gender = (ImageView) convertView.findViewById(R.id.medical_user_list_adapter_gender_image);

        holder.gender.setImageResource(images[position]);
        holder.medicalId.setText(context.getResources().getString(R.string.id) +": " + midicalIds[position]);
        holder.age.setText(context.getResources().getString(R.string.birthdate) +": "+ ages[position]);

        return convertView;
    }
}
