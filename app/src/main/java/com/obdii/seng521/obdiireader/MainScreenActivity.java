package com.obdii.seng521.obdiireader;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainScreenActivity extends AppCompatActivity implements RequestIDDialogFragment.InputDialogListener{
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button viewCodeButton = (Button) findViewById(R.id.viewCodeButton);
        viewCodeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment idFragment = new RequestIDDialogFragment();
                idFragment.show(getFragmentManager(), "viewCodesID");
            }
        });
        Button recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreenActivity.this, TripRecordActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog){
        RequestIDDialogFragment dFragment = (RequestIDDialogFragment) dialog;
        id = dFragment.getVehicleID();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        //behavior for a negative click
    }
}
