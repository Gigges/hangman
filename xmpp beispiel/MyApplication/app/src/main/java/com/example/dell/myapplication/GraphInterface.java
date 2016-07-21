package com.example.dell.myapplication;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Johannes on 20.07.2016.
 */
public class GraphInterface extends BoardActivity {

    private ImageView mIVrightHighlight;
    private ImageView mIVleftHighlight;
    private ImageView mIVpullupHighlight;
    private ImageView mIVhangboard;

    private TextView mTVhangtime;
    private TextView mTVpullups;

    @Override
    public void clearView() {
        mIVrightHighlight.setVisibility(View.INVISIBLE);
        mIVleftHighlight.setVisibility(View.INVISIBLE);
        mIVpullupHighlight.setVisibility(View.INVISIBLE);

        mTVhangtime.setText("0.0s");
        mTVpullups.setText("0");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphinterface);

        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);

        mIVpullupHighlight = (ImageView) findViewById(R.id.highlight_pullup);
        mIVrightHighlight = (ImageView) findViewById(R.id.highlight_right);
        mIVleftHighlight = (ImageView) findViewById(R.id.highlight_left);
        mIVhangboard = (ImageView) findViewById(R.id.imageView);

        mTVhangtime = (TextView) findViewById(R.id.textView_hangtime);
        mTVpullups = (TextView) findViewById(R.id.textView_pullups);
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
        } else {
            super.hanging = false;
            stopHangtime();
        }
    }

    @Override
    public void setPullup(boolean val) {
        if (val) {
            pullup = true;
            mIVpullupHighlight.setVisibility(View.VISIBLE);
        } else {
            pullup = false;
            mIVpullupHighlight.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setRightGrabbed(boolean grab) {
        rightGrabbed = grab;
    }

    @Override
    public void setLeftGrabbed(boolean grab) {
        leftGrabbed = grab;
    }

    @Override
    public void setRightGrab(int hold) {
        rightGrab = hold;
        int left = mIVhangboard.getLeft();
        int top = mIVhangboard.getTop();
        mIVrightHighlight.setVisibility(View.VISIBLE);
        switch (hold) {
            case 0:
                mIVrightHighlight.setVisibility(View.INVISIBLE);
            case 1:
                mIVrightHighlight.setX(left + 410);
                mIVrightHighlight.setY(top + 580);
                break;
            case 2:
                mIVrightHighlight.setX(left + 540);
                mIVrightHighlight.setY(top + 580);
                break;
            case 3:
                mIVrightHighlight.setX(left +430);
                mIVrightHighlight.setY(top + 480);
        }
    }

    @Override
    public void setLeftGrab(int hold) {
        leftGrab = hold;
        int left = mIVhangboard.getLeft();
        int top = mIVhangboard.getTop();
        mIVleftHighlight.setVisibility(View.VISIBLE);
        switch (hold) {
            case 0:
                mIVleftHighlight.setVisibility(View.INVISIBLE);
            case 1:
                mIVleftHighlight.setX(left + 200);
                mIVleftHighlight.setY(top + 580);
                break;
            case 2:
                mIVleftHighlight.setX(left + 70);
                mIVleftHighlight.setY(top + 580);
                break;
            case 3:
                mIVleftHighlight.setX(left + 180);
                mIVleftHighlight.setY(top + 480);
        }
    }

    @Override
    public void setRightFinger(int finger) {
        rightFinger = finger;
    }

    @Override
    public void setLeftFinger(int finger) {
        leftFinger = finger;
    }

    @Override
    public void setPullups(int pulls) {
        pullups = pulls;
        mTVpullups.setText(String.valueOf(pulls));
    }

    @Override
    public void setHangtime(long time) {
        hangtime = time;
        mTVhangtime.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
    }

    @Override
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
