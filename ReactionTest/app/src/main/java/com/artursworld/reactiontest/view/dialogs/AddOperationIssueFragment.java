package com.artursworld.reactiontest.view.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.artursworld.reactiontest.R;


public class AddOperationIssueFragment extends DialogFragment{
    private EditText operationIssueName;
    private Button btnDone;
    static String dialogboxTitle;

    public interface AddOperationIssueListener{
        void onFinishInputDialog(String inputText);
    }

    public AddOperationIssueFragment(){}

    public void setDialogTitle(String title) {
        dialogboxTitle = title;
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState){

        View view = inflater.inflate(R.layout.dialog_add_operation_issue, container);

        //---get the EditText and Button views
        operationIssueName = (EditText) view.findViewById(R.id.dialog_add_item_edittext);
        btnDone = (Button) view.findViewById(R.id.dialog_add_item_ok_btn);

        //---event handler for the button
        btnDone.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {

                //---gets the calling activity
                AddOperationIssueListener activity = (AddOperationIssueListener) getActivity();
                activity.onFinishInputDialog(operationIssueName.getText().toString());

                //---dismiss the alert
                dismiss();
            }
        });

        //---show the keyboard automatically
        operationIssueName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //---set the title for the dialog
        getDialog().setTitle(dialogboxTitle);

        return view;
    }

}
