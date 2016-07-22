package com.example.dell.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Johannes on 19.07.2016.
 */
public abstract class BoardActivity extends Activity{

    public boolean hanging = false;
    public boolean pullup = false;

    public boolean rightGrabbed = false;
    public boolean leftGrabbed = false;
    public int rightGrab = 0;
    public int leftGrab = 0;
    public int rightFinger = 0;
    public int leftFinger = 0;
    public int pullups = 0;
    public long hangtime = 0;

    public boolean incPullUps = true;
    public boolean incHangtime = true;

    public int neededRightHold = 0;
    public int neededLeftHold = 0;
    public boolean holdRestrict = false;

    public Handler hangtimeHandler = null;
    public Runnable runnable = null;
    public boolean stop = true;


    public void clearValues() {
        hanging = false;
        pullup = false;
        rightGrabbed = false;
        leftGrabbed = false;
        rightGrab = 0;
        leftGrab = 0;
        rightFinger = 0;
        leftFinger = 0;
        pullups = 0;
        hangtime = 0;
        incHangtime = true;
        incPullUps = true;
        neededRightHold = 0;
        neededLeftHold = 0;
        holdRestrict = false;
    }

    public boolean isHanging() {
        return hanging;
    }

    public boolean isPullup() {
        return pullup;
    }

    public boolean isRightGrabbed() {
        return rightGrabbed;
    }

    public boolean isLeftGrabbed() {
        return leftGrabbed;
    }

    public int getRightGrab() {
        return rightGrab;
    }

    public int getLeftGrab() {
        return leftGrab;
    }

    public int getRightFinger() {
        return rightFinger;
    }

    public int getLeftFinger() {
        return leftFinger;
    }

    public int getPullups() {
        return pullups;
    }

    public long getHangtime() {
        return hangtime;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hangtimeHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!stop && (!holdRestrict || (holdRestrict && rightGrab == neededRightHold && leftGrab == neededLeftHold))) {
                    if (incHangtime) {
                        hangtime = hangtime + 100;
                    } else {
                        hangtime = hangtime - 100;
                    }
                    onHangtimeChange();
                    hangtimeHandler.postDelayed(runnable, 100);
                }
            }
        };

    }

    public abstract void clearView();
    public abstract void onHangtimeChange();

    public void decodeMsg(byte[] buf) {
        int code = buf[0] & 0xFF; //because buf[0] is signed
        if (code > 200) {
            setRightGrabbed(true);
            setRightGrab((code - 200) / 10);
            setRightFinger(code % 10);
            setHanging(true);
            setPullups(0);
            return;
        }
        if (code == 200) {
            setRightGrabbed(false);
            setRightGrab(0);
            setRightFinger(0);
            setHanging(false);
            setPullup(false);
            return;
        }
        if (code > 100) {
            setLeftGrabbed(true);
            setLeftGrab((code - 100) / 10);
            setLeftFinger(code % 10);
            setHanging(true);
            setPullups(0);
            return;
        }
        if (code == 100) {
            setLeftGrabbed(false);
            setLeftGrab(0);
            setLeftFinger(0);
            setHanging(false);
            setPullup(false);
            return;
        }
        if (code == 91) {
            setPullup(true);
            return;
        }
        if (code == 90) {
            setPullup(false);
            stepPullUps();
            return;
        }
    }

    public void startHangtime() {
        stop = false;
        hangtimeHandler.post(runnable);
    }

    public void stopHangtime() {
        stop = true;
    }

    public abstract void setHanging(boolean val);
        /*if (val && rightGrabbed && leftGrabbed) {
            hanging = true;
            startHangtime();
        } else {
            hanging = false;
            stopHangtime();
        }
    }*/

    public abstract void setPullup(boolean val); /* {
        if (val) {
            pullup = true;
        } else {
            pullup = false;
        }
    }*/

    public abstract void setRightGrabbed(boolean grab); /*{
        rightGrabbed = grab;
    }*/

    public abstract void setLeftGrabbed(boolean grab); /*{
        leftGrabbed = grab;
    }*/

    public abstract void setRightGrab(int hold); /* {
        rightGrab = hold;
    }*/

    public abstract void setLeftGrab(int hold); /*{
        leftGrab = hold;
    }*/

    public abstract void setRightFinger(int finger); /*{
        rightFinger = finger;
    }*/

    public abstract void setLeftFinger(int finger); /*{
        leftFinger = finger;
    }*/

    public abstract void setPullups(int pulls); /*{
        pullups = pulls;
    }*/

    public abstract void setHangtime(long time); /*{
        hangtime = time;
    }*/

    public abstract void stepPullUps(); /*{
        if (incPullUps) {
            pullups = pullups + 1;
        } else {
            pullups = pullups - 1;
        }
    }*/
}
