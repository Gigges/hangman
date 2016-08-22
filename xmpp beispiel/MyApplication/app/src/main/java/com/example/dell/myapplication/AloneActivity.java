package com.example.dell.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Johannes on 20.07.2016.
 */
public class AloneActivity extends BoardActivity {

    private int points = 0;
    private boolean finished = false;

    private ImageView mIVrightHighlight;
    private ImageView mIVleftHighlight;
    private ImageView mIVpullupHighlight;
    private ImageView mIVhangboard;

    private TextView mTVhangtime;
    private TextView mTVpullups;
    private TextView mTVpoints;

    @Override
    public void clearView() {
        mIVrightHighlight.setVisibility(View.INVISIBLE);
        mIVleftHighlight.setVisibility(View.INVISIBLE);
        mIVpullupHighlight.setVisibility(View.INVISIBLE);

        mTVhangtime.setText("0.0s");
        mTVpullups.setText("0");
        mTVpoints.setText("0");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alone);

        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);

        mIVpullupHighlight = (ImageView) findViewById(R.id.highlight_pullup);
        mIVrightHighlight = (ImageView) findViewById(R.id.highlight_right);
        mIVleftHighlight = (ImageView) findViewById(R.id.highlight_left);
        mIVhangboard = (ImageView) findViewById(R.id.imageView);

        mTVhangtime = (TextView) findViewById(R.id.textView_hangtime);
        mTVpullups = (TextView) findViewById(R.id.textView_pullups);
        mTVpoints = (TextView) findViewById(R.id.textView_points);

        points = 0;
        finished = false;
    }

    @Override
    public void onHangtimeChange() {
        if (getHangtime() % 2000 == 0) {
            if (rightGrab == leftGrab) {
                if (rightGrab == 1) {
                    setPoints(points + 14);
                } else {
                    setPoints(points + 10);
                }
            } else {
                setPoints(points + 12);
            }
        }
        mTVhangtime.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
    }

    public void setPoints(int points) {
        if (! finished) {
            this.points = points;
            mTVpoints.setText(String.valueOf(points));
        }
    }

    @Override
    public void setHanging(boolean val) {
        if (val && isRightGrabbed() && isLeftGrabbed() && !finished) {
            super.hanging = true;
            setHangtime(0);
            setPoints(0);
            startHangtime();
        } else {
            super.hanging = false;
            stopHangtime();
            if (hangtime > 0) {
                finished = true;
            }
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
        float offSetRight1=(float)( mIVhangboard.getWidth()*0.55);
        float offSetRight2=(float) (mIVhangboard.getWidth()*0.77);
        float offSetRight3=(float)( mIVhangboard.getWidth()*0.7);
        float offSetTop=(float)(mIVhangboard.getHeight()*0.75);
        switch (hold) {
            case 0:
                mIVrightHighlight.setVisibility(View.INVISIBLE);
            case 1:
                mIVrightHighlight.setX(left +offSetRight1);
                mIVrightHighlight.setY(top + offSetTop);
                break;
            case 2:
                mIVrightHighlight.setX(left + offSetRight2);
                mIVrightHighlight.setY(top +offSetTop);
                break;
            case 3:
                mIVrightHighlight.setX(left +offSetRight3);
                mIVrightHighlight.setY(top + offSetTop);
        }
    }

    @Override
    public void setLeftGrab(int hold) {
        leftGrab = hold;
        int left = mIVhangboard.getLeft();
        int top = mIVhangboard.getTop();
        mIVleftHighlight.setVisibility(View.VISIBLE);
        float offSetLeft1=(float)(mIVhangboard.getWidth()*0.3);
        float offSetLeft2=(float)(mIVhangboard.getWidth()*0.1);
        float offSetLeft3=(float)(mIVhangboard.getWidth()*0.2);
        float offSetTop=(float)(mIVhangboard.getHeight()*0.75);
        switch (hold) {
            case 0:
                mIVleftHighlight.setVisibility(View.INVISIBLE);
            case 1:
                mIVleftHighlight.setX(left + offSetLeft1);
                mIVleftHighlight.setY(top + offSetTop);
                break;
            case 2:
                mIVleftHighlight.setX(left + offSetLeft2);
                mIVleftHighlight.setY(top + offSetTop);
                break;
            case 3:
                mIVleftHighlight.setX(left +offSetLeft3);
                mIVleftHighlight.setY(top + offSetTop);
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
        if (!finished) {
            pullups = pulls;
            mTVpullups.setText(String.valueOf(pulls));
        }
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
        if (rightGrab == leftGrab) {
            if (rightGrab == 1) {
                setPoints(points + 14);
            } else {
                setPoints(points + 10);
            }
        } else {
            setPoints(points + 12);
        }
        if (incPullUps) {
            setPullups(pullups + 1);
        } else {
            setPullups(pullups - 1);
        }
    }
}
