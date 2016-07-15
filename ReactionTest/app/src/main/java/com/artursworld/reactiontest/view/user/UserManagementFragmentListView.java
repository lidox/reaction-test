package com.artursworld.reactiontest.view.user;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserListAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;

import java.util.List;

public class UserManagementFragmentListView extends Fragment {
    private boolean isDualPane;
    private int currentCheckPosition = 0;
    private ListView userListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_management_fragment_list_view, container, false);
    }

    private void initMedicalUserListViewAsync() {
        new MedicalUserManager.getAllMedicalUsers(new MedicalUserManager.AsyncResponse(){

            @Override
            public void getMedicalUserList(List<MedicalUser> medicalUserResultList) {
                initMedicalUserListView(medicalUserResultList);
            }

        }, getActivity().getApplicationContext()).execute();
    }

    private void initMedicalUserListView(List<MedicalUser> userList) {
        userListView = (ListView) getActivity().findViewById(R.id.user_management_fragment_list_view);
        //TextView textView = (TextView) getActivity().findViewById(R.id.medical_user_list_view_empty_list);
        boolean isEmptyUserList = true;
        if (userList != null) {
            if (userList.size() > 0) {
                isEmptyUserList = false;
            }

            String[] medicalIds = new String[userList.size()];
            int[] ages = new int[userList.size()];
            int[] images = new int[userList.size()];


            for (int i = 0; i < userList.size(); i++) {
                medicalIds[i] = userList.get(i).getMedicalId();
                ages[i] = userList.get(i).getAge();
                images[i] = userList.get(i).getImage();
            }

            MedicalUserListAdapter adapter = new MedicalUserListAdapter(getActivity(), medicalIds, ages, images);
            userListView.setAdapter(adapter);

            userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UtilsRG.info("selected user at position: " + position);
                    showDetails(position);
                }
            });
        }
        //if (isEmptyUserList && textView != null) {
        //    textView.setText(R.string.no_user_in_db);
        //}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        UtilsRG.info(UserManagementFragmentListView.class.getSimpleName() + " onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initMedicalUserListViewAsync();

        View detailsFrame = getActivity().findViewById(R.id.details);
        isDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            currentCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (isDualPane) {
            if(userListView != null){
                userListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                showDetails(currentCheckPosition);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", currentCheckPosition);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(int index) {
        currentCheckPosition = index;

        if (isDualPane) {
            UtilsRG.info(UserManagementFragmentListView.class.getSimpleName()+ " index: " + index);
            userListView.post(new Runnable() {
                @Override
                public void run() {
                    userListView.setItemChecked(currentCheckPosition, true);
                }
            });

            // Check what fragment is currently shown, replace if needed.
            MedicalUserDetailsFragmentView details = (MedicalUserDetailsFragmentView) getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = MedicalUserDetailsFragmentView.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), MedicalUserDetailsView.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }

}
