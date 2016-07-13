package com.example.johan.hangmen;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewAnimator;

/**
 * Created by johan on 06.07.2016.
 */
public class ConnectBtActivity extends ActionBarActivity {

    Button button_bt;
    Button button_train_alone;
    public static TextView textView;
    private boolean mLogShown;

    BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        setContentView(R.layout.activity_bt_connection);
        textView = (TextView) findViewById(R.id.textView);
        button_train_alone =(Button) findViewById(R.id.button_train_alone);
        button_train_alone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(ConnectBtActivity.this,TrainAloneActivity.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? "Hide Log" : "show log");

        return super.onPrepareOptionsMenu(menu);
    }
    }

