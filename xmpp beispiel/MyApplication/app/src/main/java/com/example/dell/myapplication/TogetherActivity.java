package com.example.dell.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import services.XmppService;

/**
 * Created by Johannes on 21.07.2016.
 */
public class TogetherActivity extends BoardActivity {


    private boolean master = false;

    private ArrayList<Score> leaderboard;

    private TextView mTVpointsYou;
    private TextView mTVpointsFriend;

    private Button mButton_connect;


    private TextView mTVpullups;
    private TextView mTVhangtime;
    private TextView mTVpoints;
    private EditText mETuser;
    private EditText mETteam;
    private ListView mLVleaderboard;

    BroadcastReceiver recieve_chat;
    Connection connection;

    //---------------------------Friend

    public boolean hangingFriend = false;
    public boolean pullupFriend = false;

    public int points;
    public boolean finished=false;

    public boolean rightGrabbedFriend = false;
    public boolean leftGrabbedFriend = false;
    public int rightGrabFriend = 0;
    public int leftGrabFriend = 0;
    public int rightFingerFriend = 0;
    public int leftFingerFriend = 0;
    public int pullupsFriend = 0;
    public long hangtimeFriend = 0;
    public boolean init = false;

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
    private boolean finishedFriend = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);
        connection = XmppService.getConnection();
        XmppService.setupAndConnect(TogetherActivity.this, Util.SERVER, "", getIntent().getStringExtra("user_id"), Util.XMPP_PASSWORD);

        setContentView(R.layout.activity_together);

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



        mTVpullups = (TextView) findViewById(R.id.textView_pullupnum);
        mTVhangtime = (TextView) findViewById(R.id.textView_time);
        mTVpoints = (TextView) findViewById(R.id.textView_points);
        mETuser = (EditText) findViewById(R.id.editText_Friend);
        mETteam = (EditText) findViewById(R.id.editText_Team);
        mLVleaderboard = (ListView) findViewById(R.id.listView_leaderboard);


        username = getIntent().getStringExtra("user_id");
        mButton_connect = (Button) findViewById(R.id.button_con);
        mButton_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mETuser.getText().equals("")) {
                    XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "70;" + username);
                    mETteam.setText(username + " & " + mETuser.getText());
                    XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "60;" + mETteam.getText());
                    master = true;
                }
            }
        });



        mETuser = (EditText) findViewById(R.id.editText_Friend);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (master) {
                    XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "60;" + mETteam.getText());
                }
            }
        };

        mETteam.addTextChangedListener(watcher);

        leaderboard = new ArrayList<>();

        leaderboard.add(new Score("Mickey & Donald", 10));
        leaderboard.add(new Score("Ted & Barney", 25));
        leaderboard.add(new Score("Tim & Struppi", 50));
        leaderboard.add(new Score("Blues Brothers", 75));
        leaderboard.add(new Score("Bonnie & Clyde", 100));
        leaderboard.add(new Score("Turing & Leibniz", 150));
        leaderboard.add(new Score("Bach & Beethoven", 200));
        leaderboard.add(new Score("Parker & Davis", 250));
        leaderboard.add(new Score("Axel & Slash", 300));
        leaderboard.add(new Score("McColl & Coxsey", 350));

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mLVleaderboard.setAdapter(adapter);

        if (leaderboard.size() > 0) {
            for (Score score : leaderboard) {
                adapter.add(score.getTeamname() + "\n" + String.valueOf(score.getPoints()) + " Points");
            }
        } else {
            adapter.add("No Leaderboard Found!");
        }


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



    @Override
    public void decodeMsg(byte[] buf) {
        super.decodeMsg(buf);
        int code = buf[0] & 0xFF;
        XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, String.valueOf(code));
    }

    @Override
    public void clearView() {
        return;
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
            if (!master) {
                XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "50;" + String.valueOf(pnts));
            } else {
                setPoints(points + pnts);
            }
        }
        mTVhangtime.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
    }

    public void setPoints(int points) {
        if (!(finished && finishedFriend)) {
            this.points = points;
            mTVpoints.setText(String.valueOf(points));
            XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "55;" + String.valueOf(this.points));
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
                XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "40");
            }
        }
    }

    @Override
    public void setPullup(boolean val) {
        if (val) {
            pullup = true;
        } else {
            pullup = false;

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
    }

    @Override
    public void setLeftGrab(int hold) {
        leftGrab = hold;
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
        if (!hanging) {
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
        if (!master) {
            XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "50;" + String.valueOf(pnts));
        } else {
            setPoints(points + pnts);
        }
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
            //chill
        }
        if (code == 200) {
            //chill
        }
        if (code > 100) {
            //chill
        }
        if (code == 100) {
            //chill
        }
        if (code == 92) {
            //chill
        }
        if (code == 91) {
            //chill
        }
        if (code == 90) {
            //chill
        }
        if (code == 80) {
            //chill
        }
        if (code == 70) {
            master = false;
            mETuser.setText(array[1]);
            mETuser.setEnabled(false);
            mETteam.setEnabled(false);
            mButton_connect.setEnabled(false);
        }
        if (code == 60) {
            if ((array.length > 1)){
                mETteam.setText(array[1]);
            } else {
                mETteam.setText("");
            }
        }
        if (code == 55) {
            mTVpoints.setText(array[1]);
        }
        if (code == 50) {
            setPoints(points + Integer.valueOf(array[1]));
        }

        if (code == 40) {
           finishedFriend = true;
        }
    }






}



