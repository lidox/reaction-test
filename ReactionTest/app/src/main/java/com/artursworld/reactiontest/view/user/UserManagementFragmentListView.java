package com.artursworld.reactiontest.view.user;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicalUserListAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;

import java.util.List;

/**
 * Displays list of users and if space shows details for operation
 */
public class UserManagementFragmentListView extends Fragment {

    // display configurations if space for details
    private boolean isDualPane;
    private int currentCheckPosition = 0;

    TextView emptyText;
    private String selectedMedicalUserId;
    private ListView userListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UtilsRG.info("onCreateView on" + UserManagementFragmentListView.class.getSimpleName());
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        emptyText = (TextView) view.findViewById(R.id.empty_user_list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilsRG.info("onResume on" + UserManagementFragmentListView.class.getSimpleName());
        initMedicalUserListViewAsync(null);
    }

    /**
     * Adds all users into a list view via list adapter
     */
    private void initMedicalUserListView(List<MedicalUser> userList) {
        userListView = (ListView) getActivity().findViewById(R.id.user_management_fragment_list_view);
        if (userList.size() > 0) {
            selectedMedicalUserId = userList.get(currentCheckPosition).getMedicalId();
            UtilsRG.putString(UtilsRG.MEDICAL_USER, selectedMedicalUserId, getActivity());
        }

        boolean isEmptyUserList = true;
        if (userList != null) {
            if (userList.size() > 0) {
                isEmptyUserList = false;
            }

            final String[] medicalIds = new String[userList.size()];
            String[] birthdates = new String[userList.size()];
            int[] images = new int[userList.size()];


            for (int i = 0; i < userList.size(); i++) {
                medicalIds[i] = userList.get(i).getMedicalId();
                birthdates[i] = userList.get(i).getBirthDateAsString();
                images[i] = userList.get(i).getImage();
            }

            MedicalUserListAdapter adapter = new MedicalUserListAdapter(getActivity(), medicalIds, birthdates, images);
            userListView.setAdapter(adapter);

            userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UtilsRG.info("selected user(" + medicalIds[position] + ") at position: " + position);
                    showDetails(position, medicalIds[position]);
                }
            });
        }

        if (isEmptyUserList) {
            if (userListView != null && getActivity() != null) {
                TextView emptyText = (TextView) getView().findViewById(R.id.empty_user_list);
                if(emptyText != null)
                    emptyText.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        UtilsRG.info(UserManagementFragmentListView.class.getSimpleName() + " onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        initMedicalUserListViewAsync(savedInstanceState);
    }

    /**
     * Get all existings users and put them into a list asnchronous
     */
    private void initMedicalUserListViewAsync(final Bundle savedInstanceState) {
        new MedicalUserManager.getAllMedicalUsers(new MedicalUserManager.AsyncResponse() {

            @Override
            public void getMedicalUserList(List<MedicalUser> medicalUserResultList) {
                initMedicalUserListView(medicalUserResultList);
                if (savedInstanceState != null)
                    showDetailsFragmentIfPossible(savedInstanceState);
            }

        }, getActivity().getApplicationContext()).execute();
    }

    /**
     * Displays details if there is room on device display
     */
    private void showDetailsFragmentIfPossible(Bundle savedInstanceState) {
        View detailsView = getActivity().findViewById(R.id.details);
        isDualPane = detailsView != null && detailsView.getVisibility() == View.VISIBLE;
        UtilsRG.info("Will show dual pane: " + isDualPane);

        if (savedInstanceState != null) {
            currentCheckPosition = savedInstanceState.getInt("curChoice", 0);
            UtilsRG.info("restored selected user position= " + currentCheckPosition);
        }

        if (isDualPane && userListView != null) {
            userListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            UtilsRG.info("show details by index:" + currentCheckPosition);
            showDetails(currentCheckPosition, selectedMedicalUserId);
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
    private void showDetails(int index, String selectedUserId) {
        currentCheckPosition = index;
        selectedMedicalUserId = selectedUserId;
        UtilsRG.putString(UtilsRG.MEDICAL_USER, selectedMedicalUserId, getActivity());
        if (isDualPane) {
            UtilsRG.info(UserManagementFragmentListView.class.getSimpleName() + " index: " + index);
            userListView.post(new Runnable() {
                @Override
                public void run() {
                    userListView.setItemChecked(currentCheckPosition, true);
                }
            });

            // Check what fragment is currently shown, replace if needed.
            DetailsTabsFragment detailsFragment = (DetailsTabsFragment) getFragmentManager().findFragmentById(R.id.details);
            if (detailsFragment == null || detailsFragment.getShownIndex() != index) {
                // Make new fragment to show this selection.
                UtilsRG.info("open up new Details Fragment by index:" + index);
                detailsFragment = DetailsTabsFragment.newInstance(index, selectedUserId);


                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, detailsFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsView.class);
            intent.putExtra("index", index);
            intent.putExtra("id", selectedUserId);
            startActivity(intent);
        }
    }

}
