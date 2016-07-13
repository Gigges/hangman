package com.example.johan.hangmen.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.example.johan.hangmen.R;

/**
 * Created by johan on 06.07.2016.
 */
public class ConnectBtActivity extends ActionBarActivity {

    Button button_bt;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_connection);
        button_bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ConnectBtActivity.this, DeviceListActivity.class);
                ConnectBtActivity.this.startActivity(myIntent);

            }



        });
    }
}

