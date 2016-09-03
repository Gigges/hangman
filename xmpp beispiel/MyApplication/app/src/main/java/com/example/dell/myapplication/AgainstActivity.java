package com.example.dell.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import services.XmppService;

/**
 * Created by Johannes on 21.07.2016.
 */
public class AgainstActivity extends BoardActivity {

    private ImageView mIVrightHighlightYou;
    private ImageView mIVleftHighlightYou;
    private ImageView mIVpullupHighlightYou;
    private ImageView mIVhangboardYou;

    private ImageView mIVrightHighlightFriend;
    private ImageView mIVleftHighlightFriend;
    private ImageView mIVpullupHighlightFriend;
    private ImageView mIVhangboardFriend;

    private TextView mTVhangtime;
    private TextView mTVpullups;
    private TextView mTVpointsYou;
    private TextView mTVpointsFriend;

    private Button mButton_connect;

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

    public int points;
    public boolean finished;

    public boolean rightGrabbedFriend = false;
    public boolean leftGrabbedFriend = false;
    public int rightGrabFriend = 0;
    public int leftGrabFriend = 0;
    public int rightFingerFriend = 0;
    public int leftFingerFriend = 0;
    public int pullupsFriend = 0;
    public long hangtimeFriend = 0;
    public boolean init;

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
    public boolean stoptime = false;

    public long time = 0;


    private String username;
    private Integer pointsFriend;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init = false;


        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);
        connection = XmppService.getConnection();
        XmppService.setupAndConnect(AgainstActivity.this, Util.SERVER, "", getIntent().getStringExtra("user_id"), Util.XMPP_PASSWORD);

        setContentView(R.layout.activity_against);

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

                    hangtimeHandlerFriend.postDelayed(runnableFriend, 100);
                }
            }
        };

        timeHandler = new Handler();
        timerunnable = new Runnable() {
            @Override
            public void run() {
                if (!stoptime) {
                    time = time - 100;
                    onTimeChange();
                    timeHandler.postDelayed(timerunnable, 100);
                }
            }
        };

        recieve_chat = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("message");
                if (msg != null) {
                    decodeMsgFriend(msg);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(recieve_chat, new IntentFilter("message_recieved"));


        //mIVpullupHighlightFriend = (ImageView) findViewById(R.id.highlight_pullup_friend);
        //  mIVrightHighlightFriend = (ImageView) findViewById(R.id.highlight_right_friend);
        // mIVleftHighlightFriend = (ImageView) findViewById(R.id.highlight_left_friend);
        // mIVhangboardFriend = (ImageView) findViewById(R.id.hangboard_friend);
        mTVhangtime = (TextView) findViewById(R.id.textView_hangtime);
        mTVpullups = (TextView) findViewById(R.id.textView_pullup);
        mTVpointsYou = (TextView) findViewById(R.id.textView_points);
        mTVpointsFriend = (TextView) findViewById(R.id.textView_points_friend);
        ;
        username = getIntent().getStringExtra("user_id");
        mButton_connect = (Button) findViewById(R.id.button_con);
        mButton_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mETuser.getText().equals("")) {
                    XmppService.sendMessage(AgainstActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "70;" + username);

                }
            }
        });


        mIVpullupHighlightYou = (ImageView) findViewById(R.id.highlight_pullup_you);
        mIVrightHighlightYou = (ImageView) findViewById(R.id.highlight_right_you);
        mIVleftHighlightYou = (ImageView) findViewById(R.id.highlight_left_you);
        mIVhangboardYou = (ImageView) findViewById(R.id.hangboard_you);

        mTVYou = (TextView) findViewById(R.id.textView_you);
        // mTVFriend = (TextView) findViewById(R.id.textView_friend);
        mTVtask = (TextView) findViewById(R.id.textView_task);
        mTVtaskcontent = (TextView) findViewById(R.id.textView_taskcontent);
        mTVtime = (TextView) findViewById(R.id.textView_time);
        mETuser = (EditText) findViewById(R.id.editText_Friend);



    }

    public void setTime(long time) {
        this.time = time;
    }

    public void startTime() {
        stoptime = false;
        timeHandler.post(timerunnable);
    }

    public void stopTime() {
        stoptime = true;
    }

    private void onTimeChange() {
        mTVtime.setText(String.valueOf((float) (time / 100) / 10) + "s");
    }

    @Override
    public void decodeMsg(byte[] buf) {
        super.decodeMsg(buf);
        int code = buf[0] & 0xFF;
        XmppService.sendMessage(AgainstActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, String.valueOf(code));
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
        if (getHangtime() % 2000 == 0) {
            int pnts = 0;
            if (rightGrab == leftGrab) {
                if (rightGrab == 1) {
                    pnts = 14;
                } else {
                    pnts = 10;
                }
            } else {
                pnts = 12;
            }


            setPoints(points + pnts);

        }
        mTVhangtime.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
    }

    public void setPoints(int points) {
        if (!finished) {
            this.points = points;
            mTVpointsYou.setText(String.valueOf(points));
            XmppService.sendMessage(AgainstActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "55;" + String.valueOf(this.points));
        }
    }

    @Override
    public void setHanging(boolean val) {
        if (val && isRightGrabbed() && isLeftGrabbed() && !finished) {
            super.hanging = true;
            setHangtime(0);
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
            mIVpullupHighlightYou.setVisibility(View.VISIBLE);
            mIVpullupHighlightYou.setY((float) (mIVhangboardYou.getY() + mIVhangboardYou.getHeight() * 0.22));
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
        float offSetRight1 = (float) (mIVhangboardYou.getWidth() * 0.55);
        float offSetRight2 = (float) (mIVhangboardYou.getWidth() * 0.77);
        float offSetRight3 = (float) (mIVhangboardYou.getWidth() * 0.7);
        float offSetTop = (float) (mIVhangboardYou.getHeight() * 0.6);
        switch (hold) {
            case 0:
                mIVrightHighlightYou.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVrightHighlightYou.setX(left + offSetRight1);
                mIVrightHighlightYou.setY(top + offSetTop);
                break;
            case 2:
                mIVrightHighlightYou.setX(left + offSetRight2);
                mIVrightHighlightYou.setY(top + offSetTop);
                break;
            case 3:
                mIVrightHighlightYou.setX(left + offSetRight3);
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
        float offSetLeft1 = (float) (mIVhangboardYou.getWidth() * 0.3);
        float offSetLeft2 = (float) (mIVhangboardYou.getWidth() * 0.1);
        float offSetLeft3 = (float) (mIVhangboardYou.getWidth() * 0.2);
        float offSetTop = (float) (mIVhangboardYou.getHeight() * 0.6);
        switch (hold) {
            case 0:
                mIVleftHighlightYou.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIVleftHighlightYou.setX(left + offSetLeft1);
                mIVleftHighlightYou.setY(top + offSetTop);

                break;
            case 2:
                mIVleftHighlightYou.setX(left + offSetLeft2);
                mIVleftHighlightYou.setY(top + offSetTop);
                break;
            case 3:
                mIVleftHighlightYou.setX(left + offSetLeft3);
                mIVleftHighlightYou.setY(top + offSetTop);
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
        if (!finished) {
            pullups = pulls;
            mTVpullups.setText(String.valueOf(pulls));
        }

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
        int pnts;
        if (rightGrab == leftGrab) {
            if (rightGrab == 1) {
                pnts = 14;
            } else {
                pnts = 10;
            }
        } else {
            pnts = 12;
        }


        setPoints(points + pnts);

        if (incPullUps) {
            setPullups(pullups + 1);
        } else {
            setPullups(pullups - 1);
        }
    }

    //-------------------------------------Friend

    public void decodeMsgFriend(String buf) {
        String[] array = buf.split(";");
        int code = Integer.valueOf(array[0]);
        if (code > 200) {
           return;
        }
        if (code == 200) {
            return;
        }
        if (code > 100) {
            return;
        }
        if (code == 100) {
            return;
        }
        if (code == 92) {
            return;
        }
        if (code == 91) {
            return;
        }
        if (code == 90) {
            return;
        }
        if (code == 80) {
            return;
        }
        if (code == 70) {
            if (!init) {
                XmppService.sendMessage(AgainstActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "70;" + username);
                init = true;
            }
            mETuser.setText(array[1]);
            mETuser.setEnabled(false);
            mButton_connect.setEnabled(false);
        }
        if (code == 60) {
            //chill
        }
        if (code == 55) {
            pointsFriend = Integer.valueOf(array[1]);
            mTVpointsFriend.setText(array[1]);
        }
        if (code == 50) {
            //chill
        }
    }






    }



