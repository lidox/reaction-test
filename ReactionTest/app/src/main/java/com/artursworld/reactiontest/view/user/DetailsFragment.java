package com.artursworld.reactiontest.view.user;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;


public class DetailsFragment extends Fragment {

    private TabHost tabHost;
    public View rootView;

    public static DetailsFragment newInstance(int index, String selectedMedicalUserId) {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " called by index:"+index + " and user("+selectedMedicalUserId+")");
        DetailsFragment f = new DetailsFragment();

        // Create arguments bundle
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("id", selectedMedicalUserId);
        f.setArguments(args);

        return f;
    }

    // Retrieve the index of the currently shown work group
    public int getShownIndex() {
        int index = getArguments().getInt("index", 0);
        UtilsRG.info("Read index by arguments. Got Index="+index);
        return index;
    }

    public String getSelectedMedicalUser() {
        String selectedMedicalUserId = getArguments().getString("id", null);
        UtilsRG.info("Read selected medical user id by arguments. Got selectedMedicalUserId="+selectedMedicalUserId);
        return selectedMedicalUserId;
    }


    @Override
    public void onAttach(Context context) {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onCreateView");

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        tabHost = (TabHost) rootView.findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tag");
        spec.setIndicator(getResources().getString(R.string.information));
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return getInformationView();
            }
        });

        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tag1");
        spec.setIndicator(getResources().getString(R.string.statistics));
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                // TODO Auto-generated method stub
                return (new AnalogClock(getActivity()));
            }
        });
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tag2");
        spec.setIndicator(getResources().getString(R.string.medicaments));
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                // TODO Auto-generated method stub
                return (new AnalogClock(getActivity()));
            }
        });
        tabHost.addTab(spec);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        UtilsRG.info(DetailsFragment.class.getSimpleName() + " onDetach");
        super.onDetach();
    }

    // use startActivityForResult(...) and not getActivity().startActivityForResult(...)
    // public void clickEventOrSomething() {
    //     getActivity().startActivityForResult(...); // will only call onActivityResult(...) in Activity
    //     startActivityForResult(...); // will call onActivityResult(...) in Activity and because of the super-call also onActivityResult(...) here in Fragment
    // }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // use it here
    }

    private View getInformationView() {
        View view = null;
        LayoutInflater inflater;
        Context context = getActivity().getApplicationContext();

        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.medical_user_information_adapter, null);
        }

        if (view != null) {
            EditText operationDate = (EditText) view.findViewById(R.id.medical_user_information_date);
            if (operationDate != null) {
                operationDate.setInputType(InputType.TYPE_NULL);
                DialogHelper.onFocusOpenDatePicker(getActivity(), operationDate);
            }

            EditText inpubationTime = (EditText) view.findViewById(R.id.medical_user_information_intubation_time);
            if (inpubationTime != null) {
                inpubationTime.setInputType(InputType.TYPE_NULL);
                DialogHelper.onFocusOpenTimePicker(getActivity(), inpubationTime);
            }

            EditText wakeupTime = (EditText) view.findViewById(R.id.medical_user_information_wakeup_time);
            if (wakeupTime != null) {
                wakeupTime.setInputType(InputType.TYPE_NULL);
                DialogHelper.onFocusOpenTimePicker(getActivity(), wakeupTime);
            }

            //TODO: save stuff in db

        }
        return view;
    }


}
