package com.example.dell.myapplication;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Johannes on 20.07.2016.
 */
public class TrainingActivity extends BoardActivity {

    private ImageView mIVrightHighlight;
    private ImageView mIVleftHighlight;
    private ImageView mIVpullupHighlight;
    private ImageView mIVhangboard;

    private TextView mTVfullHangtime;
    private TextView mTVfullPullups;
    private TextView mTVtask;
    private TextView mTVtaskcontent;

    private long fullhangtime = 0;
    private int fullpullups = 0;

    private Task task = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void clearView() {
        mIVrightHighlight.setVisibility(View.INVISIBLE);
        mIVleftHighlight.setVisibility(View.INVISIBLE);
        mIVpullupHighlight.setVisibility(View.INVISIBLE);

        mTVfullHangtime.setText("0.0s");
        mTVfullPullups.setText("0");
        mTVtask.setText("Your Task");
        mTVtaskcontent.setText("0");

        fullhangtime = 0;
        fullpullups = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);

        mIVpullupHighlight = (ImageView) findViewById(R.id.highlight_pullup);
        mIVrightHighlight = (ImageView) findViewById(R.id.highlight_right);
        mIVleftHighlight = (ImageView) findViewById(R.id.highlight_left);
        mIVhangboard = (ImageView) findViewById(R.id.imageView);

        mTVfullHangtime = (TextView) findViewById(R.id.textView_hangtime);
        mTVfullPullups = (TextView) findViewById(R.id.textView_pullups);
        mTVtask = (TextView) findViewById(R.id.textView_task);
        mTVtaskcontent = (TextView) findViewById(R.id.textView_taskcontent);

        task = new Task();
        task.setTask();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onHangtimeChange() {
        task.hangtimeChange();
        if (hanging) {
            fullhangtime += 100;
        }
        mTVfullHangtime.setText(String.valueOf((float) (fullhangtime / 100) / 10) + "s");
    }

    @Override
    public void setHanging(boolean val) {
        if (val && isRightGrabbed() && isLeftGrabbed()) {
            super.hanging = true;
            startHangtime();
        } else {
            super.hanging = false;
            if (holdRestrict && neededLeftHold == 0 && neededRightHold == 0) {
                startHangtime();
            } else {
                stopHangtime();
            }
        }
    }

    @Override
    public void setPullup(boolean val) {
        if (val) {
            pullup = true;
            mIVpullupHighlight.setY((float)(mIVhangboard.getTop()+ mIVhangboard.getHeight()*0.25));
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

        Matrix matrix = new Matrix();

        matrix.reset();


        rightGrab = hold;
        int left = mIVhangboard.getLeft();
        int top = mIVhangboard.getTop();
        mIVrightHighlight.setVisibility(View.VISIBLE);
        float offSetRight1=(float)( mIVhangboard.getWidth()*0.55);
        float offSetRight2=(float) (mIVhangboard.getWidth()*0.77);
        float offSetRight3=(float)( mIVhangboard.getWidth()*0.7);
        float offSetTop=(float)(mIVhangboard.getHeight()*0.6);
        switch (hold) {
            case 0:
                mIVrightHighlight.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVrightHighlight.setX(left + offSetRight1);
                mIVrightHighlight.setY(top + offSetTop);
                break;
            case 2:
                mIVrightHighlight.setX(left + offSetRight2);
                mIVrightHighlight.setY(top + offSetTop);
                break;
            case 3:
                mIVrightHighlight.setX(left + offSetRight3);
                mIVrightHighlight.setY(top + offSetTop);
                break;
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
        float offSetTop=(float)(mIVhangboard.getHeight()*0.6);
        switch (hold) {
            case 0:
                mIVleftHighlight.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVleftHighlight.setX(left + offSetLeft1);
                mIVleftHighlight.setY(top + offSetTop);
                break;
            case 2:
                mIVleftHighlight.setX(left + offSetLeft2);
                mIVleftHighlight.setY(top + offSetTop);
                break;
            case 3:
                mIVleftHighlight.setX(left + offSetLeft3);
                mIVleftHighlight.setY(top + offSetTop);
                break;
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
        task.pullupChange();
        mTVfullPullups.setText(String.valueOf(fullpullups));
    }

    @Override
    public void setHangtime(long time) {
        hangtime = time;
    }

    @Override
    public void stepPullUps() {
        if (!hanging || (holdRestrict && (rightGrab != neededRightHold || leftGrab != neededLeftHold))) {
            return;
        }
        if (incPullUps) {
            setPullups(pullups + 1);
        } else {
            setPullups(pullups - 1);
        }
        fullpullups++;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Training Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.dell.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Training Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.dell.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class Task {
        int number = 0;
        int it = 0;

        public void setTask() {
            switch (number) {
                case 0:
                    mTVtask.setText("Grab holds 2 for 15 sec");
                    mTVtaskcontent.setText("15.0s");
                    setHangtime(15000);
                    incHangtime = false;
                    neededRightHold = 2;
                    neededLeftHold = 2;
                    holdRestrict = true;
                    break;
                case 1:
                    mTVtask.setText("Rest for (at least) 5 sec");
                    mTVtaskcontent.setText("5.0s");
                    setHangtime(5000);
                    incHangtime = false;
                    neededRightHold = 0;
                    neededLeftHold = 0;
                    holdRestrict = true;
                    break;
                default:
                    mTVtask.setText("All tasks completed! Congrats!");
            }
        }

        public void hangtimeChange() {
            switch (number) {
                case 0:
                    if (hangtime <= 0) {
                        stopHangtime();
                        number++;
                        setTask();
                    }
                    mTVtaskcontent.setText(String.valueOf((float) (hangtime / 100) / 10) + "s");
                    break;
                case 1:
                    if (hangtime <= 0) {
                        stopHangtime();
                        if (it < 3) {
                            number = (number + 1) % 2;
                            it++;
                            setTask();
                        } else {
                            number++;
                            setTask();
                        }
                    }
                    mTVtaskcontent.setText(String.valueOf((float) (hangtime / 100) / 10) + "s");
                    break;
            }
        }

        public void pullupChange() {

        }
    }
}

