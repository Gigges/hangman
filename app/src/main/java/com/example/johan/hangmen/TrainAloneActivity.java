package com.example.johan.hangmen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * Created by johan on 07.07.2016.
 */
public class TrainAloneActivity extends ActionBarActivity {
    public static TextView TVgrabbedLeft;
    public static TextView TVgrabbedRight;
    public static TextView TVpullUps;
    public static TextView TVHangtime;
    public static int pullups;
    public static boolean grabbed_left;
    public static boolean grabbed_right;

    public static int getPullups() {
        return pullups;
    }





    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_alone);
        TVgrabbedLeft = (TextView)findViewById(R.id.textView_grabbed_left);
        TVgrabbedRight= (TextView)findViewById(R.id.textView_grabbed_right);
        TVHangtime=(TextView)findViewById(R.id.textView_hangtime);
        TVpullUps=(TextView)findViewById(R.id.textView_pull_ups);
    }



    }

