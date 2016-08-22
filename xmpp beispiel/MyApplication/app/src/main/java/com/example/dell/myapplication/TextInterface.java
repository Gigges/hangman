package com.example.dell.myapplication;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by johan on 07.07.2016.
 */
public class TextInterface extends BoardActivity {

    private TextView mTVhanging;
    private TextView mTVpullup;
    private TextView mTVrightGrab;
    private TextView mTVleftGrab;
    private TextView mTVrightFinger;
    private TextView mTVleftFinger;
    private TextView mTVpullups;
    private TextView mTVhangtime;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textinterface);

        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);

        mTVhanging = (TextView) findViewById(R.id.textView_hanging);
        mTVpullup = (TextView) findViewById(R.id.textView_pullup);
        mTVrightGrab = (TextView) findViewById(R.id.textView_gr_right);
        mTVleftGrab = (TextView) findViewById(R.id.textView_gr_left);
        mTVrightFinger = (TextView) findViewById(R.id.textView_fi_right);
        mTVleftFinger = (TextView) findViewById(R.id.textView_fi_left);
        mTVhangtime = (TextView) findViewById(R.id.textView_hangtime);
        mTVpullups = (TextView) findViewById(R.id.textView_num_pullup);
    }

    @Override
    public void clearView() {
        mTVhanging.setText("No");
        mTVpullup.setText("No");
        mTVrightGrab.setText("0");
        mTVleftGrab.setText("0");
        mTVrightFinger.setText("0");
        mTVleftFinger.setText("0");
        mTVpullups.setText("0");
        mTVhangtime.setText("0.0");
    }


    @Override
    public void onHangtimeChange() {
        mTVhangtime.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
    }

    @Override
    public void setHanging(boolean val) {
        if (val && isRightGrabbed() && isLeftGrabbed()) {
            super.hanging = true;
            setHangtime(0);
            startHangtime();
            mTVhanging.setText("Yes");
        } else {
            super.hanging = false;
            stopHangtime();
            mTVhanging.setText("No");
        }
    }

    public void setPullup(boolean val) {
        if (val) {
            pullup = true;
            mTVpullup.setText("Yes");
        } else {
            pullup = false;
            mTVpullup.setText("No");
        }
    } 
    
    public void setRightGrabbed(boolean grab) {
        rightGrabbed = grab;
    }

    public void setLeftGrabbed(boolean grab) {
        leftGrabbed = grab;
    }

    public void setRightGrab(int hold) {
        rightGrab = hold;
        mTVrightGrab.setText(String.valueOf(hold));
    }

    public void setLeftGrab(int hold) {
        leftGrab = hold;
        mTVleftGrab.setText(String.valueOf(hold));
    }

    public void setRightFinger(int finger) {
        rightFinger = finger;
        mTVrightFinger.setText(String.valueOf(finger));
    }

    public void setLeftFinger(int finger) {
        leftFinger = finger;
        mTVleftFinger.setText(String.valueOf(finger));
    }

    public void setPullups(int pulls) {
        pullups = pulls;
        mTVpullups.setText(String.valueOf(pulls));
    }

    public void setHangtime(long time) {
        hangtime = time;
        mTVhangtime.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
    }

    public void stepPullUps() {
        if (!hanging) {
            return;
        }
        if (incPullUps) {
            setPullups(pullups + 1);
        } else {
            setPullups(pullups - 1);
        }
    }

}

