package com.example.johan.hangmen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by johan on 13.07.2016.
 */
public class TrainTogetherActivity extends ActionBarActivity{

    TextView TVpullUpsYou;
    TextView TVpullUpsOpp;
    TextView TVGLYou;
    TextView TVGLOpp;
    TextView TVGRYou;
    TextView TVGROpp;
    TextView TVhangtimeYou;
    TextView TVhantimeOpp;
   public static EditText ETuser;
    OurMsg message;

    BroadcastReceiver recieve_chat;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_train_together);

        TVpullUpsYou=(TextView)findViewById(R.id.textViewPullupsYou);
        TVpullUpsOpp=(TextView)findViewById(R.id.textViewPullupsOpponent);

        TVGLYou=(TextView)findViewById(R.id.textViewGLyou);
        TVGLOpp=(TextView)findViewById(R.id.textViewGrabbedLeftOpp);

        TVGRYou=(TextView)findViewById(R.id.textViewGRyou);
        TVGROpp=(TextView)findViewById(R.id.textViewGrabbedRightOpp);

        TVhangtimeYou=(TextView)findViewById(R.id.textViewHangimteYou);
        TVhantimeOpp=(TextView)findViewById(R.id.textViewHangtimeIOpp);

        ETuser=(EditText)findViewById(R.id.editTextOpp);



        recieve_chat=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String message=intent.getStringExtra("message");

                Log.d("pavan","in local braod "+message);
               processMessage(message);


            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(recieve_chat, new IntentFilter("message_recieved"));




    }

    private void processMessage(String message) {
        String s = message.substring(1, message.length()-1);
        String[] array = s.split(",");
        TVpullUpsOpp.setText(Integer.valueOf(array[0]));
        TVhantimeOpp.setText(Integer.valueOf(array[1]));
        TVGLOpp.setText(Integer.valueOf(array[2]));
        TVGRYou.setText(Integer.valueOf(array[3]));
    }

    public static String getUser(){
        String s= ETuser.getText().toString();
        return s;
    }
}
