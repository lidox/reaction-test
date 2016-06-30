package com.artursworld.reactiontest.view.user;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.view.dialogs.DateDialog;

public class AddMedicalUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_user);

        Button btn = (Button) findViewById(R.id.gender_btn);
        registerForContextMenu(btn);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Action 1"){function1(item.getItemId());}
        else if(item.getTitle()=="Action 2"){function2(item.getItemId());}
        else {return false;}
        return true;
    }

    public void function1(int id){
        Toast.makeText(this, "function 1 called", Toast.LENGTH_SHORT).show();
    }
    public void function2(int id){
        Toast.makeText(this, "function 2 called", Toast.LENGTH_SHORT).show();
    }

    public void onStart(){
        super.onStart();
        try {
            EditText txtDate=(EditText)findViewById(R.id.birthdate_picker);
            txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                public void onFocusChange(View view, boolean hasfocus){
                    if(hasfocus){
                        DateDialog dialog = new DateDialog(view);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                    }
                }

            });
        }
        catch (Exception e){
            UtilsRG.error("Could not load DateDialog");
        }
    }
}
