package com.artursworld.reactiontest.view.user;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.adapters.MedicamentListAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.Medicament;
import com.artursworld.reactiontest.model.persistence.manager.MedicamentManager;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This fragment shows a list of medicament's
 */
public class MedicamentListFragment extends Fragment implements Observer {

    // view
    private ListView medicamentListView = null;
    private TextView emptyMedicamentTextView = null;
    private FloatingActionButton addMedicamentBtn = null;

    // logic
    private List<Medicament> medicamentList = null;
    private String operationIssue = null;
    private MedicamentListFragment self = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UtilsRG.info(MedicamentListFragment.class.getSimpleName() + " onCreateView()");
        View view = inflater.inflate(R.layout.fragment_medicament_list, container, false);
        self = this;
        medicamentListView = (ListView) view.findViewById(R.id.medicament_list_view);
        emptyMedicamentTextView = (TextView) view.findViewById(R.id.empty_medicament_list);
        addMedicamentBtn = (FloatingActionButton) view.findViewById(R.id.add_medicament_btn);
        addOnAddMedicamentButtonListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilsRG.info(MedicamentListFragment.class.getSimpleName() + " onResume()");
        addMedicamentToListViewAsync();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UtilsRG.info(MedicamentListFragment.class.getSimpleName() + " onActivityCreated()");
    }

    /**
     * Add click listener to button
     */
    private void addOnAddMedicamentButtonListener() {
        if(addMedicamentBtn != null){
            addMedicamentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilsRG.info("onAddMedicamentButtonClick() has been clicked");
                    MedicamentDetailDialog dialog = new MedicamentDetailDialog(getActivity(), null);
                    dialog.addObserver(self);
                }
            });
        }
    }


    /**
     * First initializes medicament list and than adds medicament's into the list view
     */
    private void addMedicamentToListViewAsync() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (getActivity() != null) {
                    operationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, getActivity());
                    medicamentList = new MedicamentManager(getActivity().getApplicationContext()).getMedicamentList(operationIssue, "ASC");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (medicamentList != null) {
                    addMedicamentsToListView(medicamentList);
                }
            }
        }.execute();
    }

    /**
     * Adds all medicament's from medicament list into a list view via list adapter
     */
    private void addMedicamentsToListView(final List<Medicament> medicamentList) {
        boolean isEmptyUserList = true;
        if (medicamentList != null) {
            if (medicamentList.size() > 0) {
                isEmptyUserList = false;
            }

            MedicamentListAdapter adapter = new MedicamentListAdapter(getActivity(), medicamentList);
            medicamentListView.setAdapter(adapter);

            medicamentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UtilsRG.info("selected medicament=" + medicamentList.get(position) + " at position: " + position);
                }
            });
            //registerForContextMenu(medicamentListView); //TODO: hier
        }

        showOrHideEmptyMessage(medicamentList, isEmptyUserList);
    }

    /**
     * Shows a message if no medicament in database
     *
     * @param medicamentList  the medicament list to show if not empty
     * @param isEmptyUserList true if list is empty or null otherwise false
     */
    private void showOrHideEmptyMessage(List<Medicament> medicamentList, boolean isEmptyUserList) {
        if (isEmptyUserList) {
            if (medicamentList != null && getActivity() != null) {
                TextView emptyText = (TextView) getView().findViewById(R.id.empty_medicament_list);
                if (emptyText != null)
                    emptyText.setVisibility(View.VISIBLE);
            }
        } else {
            if (medicamentList != null && getActivity() != null) {
                TextView emptyText = (TextView) getView().findViewById(R.id.empty_medicament_list);
                if (emptyText != null)
                    emptyText.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        UtilsRG.info("Got update by dialog");
        addMedicamentToListViewAsync();
    }
}
