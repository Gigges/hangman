package com.example.dell.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        public void onCreate(Bundle savedInstanceState){
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
                    break;
                case 1:
                    mIVrightHighlight.setX(left + 410);
                    mIVrightHighlight.setY(top + 200);
                    break;
                case 2:
                    mIVrightHighlight.setX(left + 540);
                    mIVrightHighlight.setY(top + 200);
                    break;
                case 3:
                    mIVrightHighlight.setX(left +430);
                    mIVrightHighlight.setY(top + 100);
                    break;
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
                    break;
                case 1:
                    mIVleftHighlight.setX(left + 200);
                    mIVleftHighlight.setY(top + 200);
                    break;
                case 2:
                    mIVleftHighlight.setX(left + 70);
                    mIVleftHighlight.setY(top + 200);
                    break;
                case 3:
                    mIVleftHighlight.setX(left + 180);
                    mIVleftHighlight.setY(top + 100);
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


    private class Task {
        int number = 0;
        int it = 0;

        public void setTask(){
            switch (number) {
                case 0:
                    mTVtask.setText("Grab holds 3 for 15 sec");
                    mTVtaskcontent.setText("15.0s");
                    setHangtime(15000);
                    incHangtime = false;
                    neededRightHold = 3;
                    neededLeftHold = 3;
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

        public void hangtimeChange(){
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

