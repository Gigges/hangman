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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringEncoder;

import java.util.ArrayList;

import services.XmppService;

/**
 * Created by Johannes on 21.07.2016.
 */
public class TogetherActivity extends BoardActivity {

    private Button mButton_connect;

    private TextView mTVpullups;
    private TextView mTVhangtime;
    private TextView mTVpoints;
    private EditText mETuser;
    private EditText mETteam;
    private ListView mLVleaderboard;

    private boolean master = false;

    private ArrayList<Score> leaderboard;

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
    public String username = "";

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
    public int points = 0;
    public boolean finished = false;

    public Task task;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);
        setContentView(R.layout.activity_together);
        connection = XmppService.getConnection();
        XmppService.setupAndConnect(TogetherActivity.this, Util.SERVER, "", getIntent().getStringExtra("user_id"), Util.XMPP_PASSWORD);
        username = getIntent().getStringExtra("user_id");

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
                if (!stoptime) {
                    time = time - 100;
                    onTimeChange();
                    timeHandler.postDelayed(timerunnable, 100);
                }
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

        mButton_connect = (Button) findViewById(R.id.button_con);
        mButton_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mETuser.getText().equals("")){
                    task.start();
                    XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, " 70 " + username);
                    mETteam.setText(username + " & " + mETuser.getText());
                    master = true;
                }
            }
        });

        mTVpullups = (TextView) findViewById(R.id.textView_pullups);
        mTVhangtime = (TextView) findViewById(R.id.textView_hangtime);
        mTVpoints = (TextView) findViewById(R.id.textView_points);
        mETuser = (EditText) findViewById(R.id.editText_Friend);
        mETteam = (EditText) findViewById(R.id.editText_Team);
        mLVleaderboard = (ListView) findViewById(R.id.listView_leaderboard);
        task = new Task();
        task.setTask();

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
                    XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, " 60 " + mETteam.getText());
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

    private void onTimeChange() {
        task.onTimeChange(time);
        mTVhangtime.setText(String.valueOf((float) (time / 100) / 10) + "s");
    }

    @Override
    public void decodeMsg(byte[] buf) {
        super.decodeMsg(buf);
        int code = buf[0] & 0xFF;
        XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, String.valueOf(code));
    }

    @Override
    public void clearView() {
        mTVpoints.setText("0");
        mTVpullups.setText("0");
        mTVhangtime.setText("0");
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
            XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, " 50 "+String.valueOf(pnts));
            setPoints(points + pnts);
            mTVhangtime.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
        }
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
        XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, " 50 "+String.valueOf(pnts));
        setPoints(points + pnts);
        if (incPullUps) {
            setPullups(pullups + 1);
        } else {
            setPullups(pullups - 1);
        }
    }

    //-------------------------------------Friend

    public void decodeMsgFriend(String buf) {
        int code = Integer.valueOf(buf.substring(0, 2));
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
        if (code == 70) {
            master = false;
            mETuser.setText(buf.substring(3));
            mETuser.setEnabled(false);
            mETteam.setEnabled(false);
        }
        if (code == 60) {
            mETteam.setText(buf.substring(3));
        }
        if (code == 50) {
            setPoints(points + Integer.valueOf(buf.substring(3)));
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
        } else {
            pullupFriend = false;
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
    }

    public void setLeftGrabFriend(int hold) {
        leftGrabFriend = hold;
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
        long tasktime = 15000;
        boolean lastcompleted = false;
        boolean completed = true;
        boolean completedFriend = true;

        Runnable wait = new Runnable() {
            @Override
            public void run() {
                setTask();
                start();
            }};

        Handler waithandler = new Handler();

        public void setTask() {
            if (i == 0) {
                //mTVtask.setText("Get Ready!");
            } else {
                //mTVtask.setText("Do " + String.valueOf(i) + " Pull Ups!");
            }
            //mTVtime.setText(String.valueOf((float) (time / 100) / 10) + "s");
            if (i != 0) {
                completed = false;
                completedFriend = false;
            } else {
                completed = true;
                completedFriend = true;
            }
            setTime(tasktime);
            setPullups(0);
            setPullupsFriend(0);
        }

        public void onTimeChange(long time) {
            if ( time <= 0 ) {
                stopTime();
                if (!completed && !completedFriend) {
                    if (i == 1) {
                        //mTVtask.setText("Draw!");
                    } else {
                        if (lastcompleted) {
                            //mTVtask.setText("You win, because you finished first in the round before!");
                        } else {
                           // mTVtask.setText("Your Friend wins, because he finished first in the round before!");
                        }
                    }
                } else {
                    if (!completedFriend && completed) {
                        //mTVtask.setText("You win, because your friend did not finish this round!");
                    } else {
                        if (completedFriend && !completed) {
                            //mTVtask.setText("Your Friend wins, because you did not finish this round!");
                        } else {
                            i++;
                            if (i % 5 == 0) {
                                tasktime = tasktime + 7000;
                            } else {
                                tasktime = tasktime + 5000;
                            }
                            waithandler.postDelayed(wait, 200);
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
                XmppService.sendMessage(TogetherActivity.this, mETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat, "80");
            } else {
                //mTVtask.setText("Do " + String.valueOf(i - pulls) + " Pull Ups!");
            }
            //mTVtaskcontent.setText(String.valueOf(pulls));
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
