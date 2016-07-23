package com.example.dell.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import services.XmppService;

/**
 * Created by Johannes on 21.07.2016.
 */
public class CoopActivity extends BoardActivity {

    private ImageView mIVrightHighlightYou;
    private ImageView mIVleftHighlightYou;
    private ImageView mIVpullupHighlightYou;
    private ImageView mIVhangboardYou;

    private ImageView mIVrightHighlightFriend;
    private ImageView mIVleftHighlightFriend;
    private ImageView mIVpullupHighlightFriend;
    private ImageView mIVhangboardFriend;

    private TextView mTVYou;
    private TextView mTVFriend;
    private TextView mTVtask;
    private TextView mTVtaskcontent;
    private TextView mTVtime;
    private EditText mETuser;

    BroadcastReceiver recieve_chat;
    Connection connection;

    //---------------------------Friend

    public boolean hangingFriend = false;
    public boolean pullupFriend = false;

    public boolean rightGrabbedFriend = false;
    public boolean leftGrabbedFriend = false;
    public int rightGrabFriend = 0;
    public int leftGrabFriend = 0;
    public int rightFingerFriend = 0;
    public int leftFingerFriend = 0;
    public int pullupsFriend = 0;
    public long hangtimeFriend = 0;

    public boolean incPullUpsFriend = true;
    public boolean incHangtimeFriend = true;

    public int neededRightHoldFriend = 0;
    public int neededLeftHoldFriend = 0;
    public boolean holdRestrictFriend = false;

    public Handler hangtimeHandlerFriend = null;
    public Runnable runnableFriend = null;
    public boolean stopFriend = true;

    public Handler timeHandler = null;
    public Runnable timerunnable = null;
    public boolean stoptime = true;

    public long time = 0;

    public Task task;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coop);

        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);

        connection = XmppService.getConnection();
        XmppService.setupAndConnect(CoopActivity.this, Util.SERVER, "", getIntent().getStringExtra("user_id"), Util.XMPP_PASSWORD);

        setContentView(R.layout.activity_coop);

        hangtimeHandlerFriend = new Handler();
        runnableFriend = new Runnable() {
            @Override
            public void run() {
                if (!stopFriend && (!holdRestrictFriend || (holdRestrictFriend && rightGrabFriend == neededRightHoldFriend && leftGrabFriend == neededLeftHoldFriend))) {
                    if (incHangtimeFriend) {
                        hangtimeFriend = hangtimeFriend + 100;
                    } else {
                        hangtimeFriend = hangtimeFriend - 100;
                    }
                    onHangtimeChangeFriend();
                    hangtimeHandlerFriend.postDelayed(runnableFriend, 100);
                }
            }
        };

        timeHandler = new Handler();
        timerunnable = new Runnable() {
            @Override
            public void run() {
                time = time - 100;
                onTimeChange();
                timeHandler.postDelayed(runnable, 100);
            }
        };

        recieve_chat=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message");
            if (msg != null) {
                decodeMsgFriend(msg);
            }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(recieve_chat, new IntentFilter("message_recieved"));


        task = new Task();
        task.setTask();
        task.start();

        mIVpullupHighlightFriend = (ImageView) findViewById(R.id.highlight_pullup_friend);
        mIVrightHighlightFriend = (ImageView) findViewById(R.id.highlight_right_friend);
        mIVleftHighlightFriend = (ImageView) findViewById(R.id.highlight_left_friend);
        mIVhangboardFriend = (ImageView) findViewById(R.id.hangboard_friend);

        mIVpullupHighlightYou = (ImageView) findViewById(R.id.highlight_pullup_you);
        mIVrightHighlightYou = (ImageView) findViewById(R.id.highlight_right_you);
        mIVleftHighlightYou = (ImageView) findViewById(R.id.highlight_left_you);
        mIVhangboardYou = (ImageView) findViewById(R.id.hangboard_you);

        mTVYou = (TextView) findViewById(R.id.textView_you);
        mTVFriend = (TextView) findViewById(R.id.textView_friend);
        mTVtask = (TextView) findViewById(R.id.textView_task);
        mTVtaskcontent = (TextView) findViewById(R.id.textView_taskcontent);
        mTVtime = (TextView) findViewById(R.id.textView_time);
        mETuser=(EditText)findViewById(R.id.editText_Friend);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void startTime() {
        stoptime = false;
        timeHandler.post(timerunnable);
    }

    private void onTimeChange() {
        task.onTimeChange(time);
        mTVtime.setText(String.valueOf((float) (time / 100) / 10) + "s");
    }

    @Override
    public void decodeMsg(byte[] buf) {
        super.decodeMsg(buf);
        int code = buf[0] & 0xFF;
        XmppService.sendMessage(CoopActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, String.valueOf(code));
    }

    @Override
    public void clearView() {
        mIVrightHighlightYou.setVisibility(View.INVISIBLE);
        mIVleftHighlightYou.setVisibility(View.INVISIBLE);
        mIVpullupHighlightYou.setVisibility(View.INVISIBLE);

        mIVrightHighlightFriend.setVisibility(View.INVISIBLE);
        mIVleftHighlightFriend.setVisibility(View.INVISIBLE);
        mIVpullupHighlightFriend.setVisibility(View.INVISIBLE);

        mTVtask.setText("Your Task");
        mTVtaskcontent.setText("0");
        mTVtime.setText("0");
    }

    @Override
    public void onHangtimeChange() {
    }


    @Override
    public void setHanging(boolean val) {
        if (val && isRightGrabbed() && isLeftGrabbed()) {
            super.hanging = true;
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
            mIVpullupHighlightYou.setVisibility(View.VISIBLE);
        } else {
            pullup = false;
            mIVpullupHighlightYou.setVisibility(View.INVISIBLE);
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
        int left = mIVhangboardYou.getLeft();
        int top = mIVhangboardYou.getTop();
        mIVrightHighlightYou.setVisibility(View.VISIBLE);
        switch (hold) {
            case 0:
                mIVrightHighlightYou.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVrightHighlightYou.setX(left + 410);
                mIVrightHighlightYou.setY(top + 200);
                break;
            case 2:
                mIVrightHighlightYou.setX(left + 540);
                mIVrightHighlightYou.setY(top + 200);
                break;
            case 3:
                mIVrightHighlightYou.setX(left +430);
                mIVrightHighlightYou.setY(top + 100);
                break;
        }
    }

    @Override
    public void setLeftGrab(int hold) {
        leftGrab = hold;
        int left = mIVhangboardYou.getLeft();
        int top = mIVhangboardYou.getTop();
        mIVleftHighlightYou.setVisibility(View.VISIBLE);
        switch (hold) {
            case 0:
                mIVleftHighlightYou.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVleftHighlightYou.setX(left + 200);
                mIVleftHighlightYou.setY(top + 200);
                break;
            case 2:
                mIVleftHighlightYou.setX(left + 70);
                mIVleftHighlightYou.setY(top + 200);
                break;
            case 3:
                mIVleftHighlightYou.setX(left + 180);
                mIVleftHighlightYou.setY(top + 100);
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
        task.pullupChange(pulls);
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
    }

    //-------------------------------------Friend

    public void decodeMsgFriend(String buf) {
        int code = Integer.valueOf(buf);
        if (code > 200) {
            setRightGrabbedFriend(true);
            setRightGrabFriend((code - 200) / 10);
            setRightFingerFriend(code % 10);
            setHangingFriend(true);
            setPullupsFriend(0);
            return;
        }
        if (code == 200) {
            setRightGrabbedFriend(false);
            setRightGrabFriend(0);
            setRightFingerFriend(0);
            setHangingFriend(false);
            setPullupFriend(false);
            return;
        }
        if (code > 100) {
            setLeftGrabbedFriend(true);
            setLeftGrabFriend((code - 100) / 10);
            setLeftFingerFriend(code % 10);
            setHangingFriend(true);
            setPullupsFriend(0);
            return;
        }
        if (code == 100) {
            setLeftGrabbedFriend(false);
            setLeftGrabFriend(0);
            setLeftFingerFriend(0);
            setHangingFriend(false);
            setPullupFriend(false);
            return;
        }
        if (code == 92) {
            setPullupFriend(false);
            return;
        }
        if (code == 91) {
            setPullupFriend(true);
            return;
        }
        if (code == 90) {
            setPullupFriend(false);
            stepPullUpsFriend();
            return;
        }
        if (code == 80) {
            task.onCompletedFriend();
        }
    }

    public void onHangtimeChangeFriend() {
    }

    public void setHangingFriend(boolean val) {
        if (val && rightGrabbedFriend && leftGrabbedFriend) {
            super.hanging = true;
            startHangtimeFriend();
        } else {
            super.hanging = false;
            stopHangtimeFriend();
        }
    }

    private void stopHangtimeFriend() {
        stopFriend = false;
    }

    private void startHangtimeFriend() {
        stopFriend = false;
        hangtimeHandlerFriend.post(runnableFriend);
    }

    public void setPullupFriend(boolean val) {
        if (val) {
            pullupFriend = true;
            mIVpullupHighlightFriend.setVisibility(View.VISIBLE);
        } else {
            pullupFriend = false;
            mIVpullupHighlightFriend.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightGrabbedFriend(boolean grab) {
        rightGrabbedFriend = grab;
    }

    public void setLeftGrabbedFriend(boolean grab) {
        leftGrabbedFriend = grab;
    }

    public void setRightGrabFriend(int hold) {
        rightGrabFriend = hold;
        int left = mIVhangboardFriend.getLeft();
        int top = mIVhangboardFriend.getTop();
        mIVrightHighlightFriend.setVisibility(View.VISIBLE);
        switch (hold) {
            case 0:
                mIVrightHighlightFriend.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVrightHighlightFriend.setX(left + 410);
                mIVrightHighlightFriend.setY(top + 200);
                break;
            case 2:
                mIVrightHighlightFriend.setX(left + 540);
                mIVrightHighlightFriend.setY(top + 200);
                break;
            case 3:
                mIVrightHighlightFriend.setX(left +430);
                mIVrightHighlightFriend.setY(top + 100);
                break;
        }
    }

    public void setLeftGrabFriend(int hold) {
        leftGrabFriend = hold;
        int left = mIVhangboardFriend.getLeft();
        int top = mIVhangboardFriend.getTop();
        mIVleftHighlightFriend.setVisibility(View.VISIBLE);
        switch (hold) {
            case 0:
                mIVleftHighlightFriend.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVleftHighlightFriend.setX(left + 200);
                mIVleftHighlightFriend.setY(top + 200);
                break;
            case 2:
                mIVleftHighlightFriend.setX(left + 70);
                mIVleftHighlightFriend.setY(top + 200);
                break;
            case 3:
                mIVleftHighlightFriend.setX(left + 180);
                mIVleftHighlightFriend.setY(top + 100);
                break;
        }
    }

    public void setRightFingerFriend(int finger) {
        rightFingerFriend = finger;
    }

    public void setLeftFingerFriend(int finger) {
        leftFingerFriend = finger;
    }

    public void setPullupsFriend(int pulls) {
        pullupsFriend = pulls;
        task.pullupChangeFriend(pulls);
    }

    public void setHangtimeFriend(long time) {
        hangtimeFriend = time;
    }

    public void stepPullUpsFriend() {
        if (!hangingFriend || (holdRestrictFriend && (rightGrabFriend != neededRightHoldFriend || leftGrabFriend != neededLeftHoldFriend))) {
            return;
        }
        if (incPullUpsFriend) {
            setPullups(pullupsFriend + 1);
        } else {
            setPullups(pullupsFriend - 1);
        }
    }

    //-----------------------------------------Task

    public class Task {
        int i = 0;
        long tasktime = 15;
        boolean lastcompleted = false;
        boolean completed = true;
        boolean completedFriend = true;

        public void setTask() {
            mTVtask.setText("Do " + String.valueOf(i) + "Pull Ups!");
            mTVtime.setText(String.valueOf(tasktime) + ".0s");
            completed = false;
            completedFriend = false;
            setTime(tasktime);
            setPullups(0);
            setPullupsFriend(0);
        }

        public void onTimeChange(long time) {
            if ( time <= 0 ) {
                if (!completed && !completedFriend) {
                    if (i == 1) {
                        mTVtask.setText("Draw!");
                    } else {
                        if (lastcompleted) {
                            mTVtask.setText("You win, because you finished first in the round before!");
                        } else {
                            mTVtask.setText("Your Friend wins, because he finished first in the round before!");
                        }
                    }
                } else {
                    if (!completedFriend && completed) {
                        mTVtask.setText("You win, because your friend did not finish this round!");
                    } else {
                        if (completedFriend && !completed) {
                            mTVtask.setText("Your Friend wins, because you did not finish this round!");
                        } else {
                            i++;
                            if (i % 5 == 0) {
                                tasktime = tasktime + 10;
                            } else {
                                tasktime = tasktime + 5;
                            }
                            setTask();
                            start();
                        }
                    }
                }
            }
        }

        public void pullupChange(int pulls) {
            if (pulls >= i) {
                completed = true;
                if (completedFriend) {
                    lastcompleted = false;
                }
                XmppService.sendMessage(CoopActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "80");
            } else {
                mTVtask.setText("Do " + String.valueOf(i - pulls) + "Pull Ups!");
            }
        }

        public void pullupChangeFriend(int pulls) {

        }

        public void onCompletedFriend() {
            completedFriend = true;
            if (completed) {
                lastcompleted = true;
            }
        }

        public void start(){
            startTime();
        }
    }
}
