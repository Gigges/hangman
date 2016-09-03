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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import services.XmppService;


/**
 * Created by johan on 13.07.2016.
 */
public class TrainTogetherActivity extends BoardActivity {

    TextView TVpullUpsYou;
    TextView TVpullUpsOpp;
    TextView TVGLYou;
    TextView TVGLOpp;
    TextView TVGRYou;
    TextView TVGROpp;
    TextView TVhangtimeYou;
    TextView TVhangtimeOpp;
    Button button_connect;


    public static EditText ETuser;
    OurMsg message;
    OurMsg OldMessage;
    Connection connection;


    Integer pullYou;
    Integer pullOpp;


    BroadcastReceiver recieve_chat;
    private boolean rightGrabbedOpp;
    private boolean leftGrabbedOpp;
    private int rightGrabOpp;
    private int leftGrabOpp;
    private long hangtimeOpp;
    private boolean hangingOpp;
    private boolean stopOpp;
    private Runnable runnableOpp;
    private Handler hangtimeHandlerOpp;
    private boolean holdRestrictOpp;
    private int neededRightHoldOpp;
    private int neededLeftHoldOpp;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);

        connection = XmppService.getConnection();
        XmppService.setupAndConnect(TrainTogetherActivity.this,Util.SERVER,"",getIntent().getStringExtra("user_id"),Util.XMPP_PASSWORD);

        setContentView(R.layout.activity_train_together);

        TVpullUpsYou=(TextView)findViewById(R.id.textViewPullupsYou);
        TVpullUpsOpp=(TextView)findViewById(R.id.textViewPullupsOpponent);

        TVGLYou=(TextView)findViewById(R.id.textViewGLyou);
        TVGLOpp=(TextView)findViewById(R.id.textViewGrabbedLeftOpp);

        TVGRYou=(TextView)findViewById(R.id.textViewGRyou);
        TVGROpp=(TextView)findViewById(R.id.textViewGrabbedRightOpp);

        TVhangtimeYou=(TextView)findViewById(R.id.textViewHangimteYou);
        TVhangtimeOpp=(TextView)findViewById(R.id.textViewHangtimeIOpp);
        button_connect = (Button) findViewById(R.id.button_connect);
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    XmppService.sendMessage(TrainTogetherActivity.this,ETuser.getText()+Util.SUFFIX_CHAT, Message.Type.chat,"connected with  "+getIntent().getStringExtra("user_id"));
            }
        });
        ETuser=(EditText)findViewById(R.id.editTextOpp);


        Byte code=0;

        recieve_chat=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                    Log.d("trainTogether", "message Received");
                    String msg = intent.getStringExtra("message");
                    if (msg != null) {
                    Log.d("pavan", "in local braod " + message);
                    ProcessAndDisplayMessage(msg);
                     //   XmppService.sendMessage(TrainTogetherActivity.this,ETuser.getText()+Util.SUFFIX_CHAT, Message.Type.chat,message.toString()+"hier");
                }

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(recieve_chat, new IntentFilter("message_recieved"));

        hangtimeHandlerOpp = new Handler();
        runnableOpp = new Runnable() {
            @Override
            public void run() {
                if (!stopOpp && (!holdRestrictOpp || (holdRestrictOpp && rightGrabOpp == neededRightHoldOpp && leftGrabOpp == neededLeftHoldOpp))) {
                    if (incHangtime) {
                        hangtimeOpp = hangtimeOpp + 100;
                    } else {
                        hangtimeOpp = hangtimeOpp - 100;
                    }
                    onHangtimeChangeOpp();
                    hangtimeHandlerOpp.postDelayed(runnableOpp, 100);
                }
            }
        };


    }

    private void onHangtimeChangeOpp() {
        long hangtime = getHangtimeOpp();
        //  XmppService.sendMessage(TrainTogetherActivity.this,ETuser.getText()+Util.SUFFIX_CHAT, Message.Type.chat,message.toString());
        TVhangtimeOpp.setText(String.valueOf((float) (hangtime / 100) / 10) + "s");
    }

    @Override
    public void clearView() {

    }

    @Override
    public void onHangtimeChange() {
        long hangtime = getHangtime();
        //  XmppService.sendMessage(TrainTogetherActivity.this,ETuser.getText()+Util.SUFFIX_CHAT, Message.Type.chat,message.toString());
        TVhangtimeYou.setText(String.valueOf((float) (hangtime / 100) / 10) + "s");
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
        TVGRYou.setText(String.valueOf(hold));
    }

    @Override
    public void setLeftGrab(int hold) {
        leftGrab = hold;
        TVGLYou.setText(String.valueOf(hold));
    }

    @Override
    public void setRightFinger(int finger) {

    }

    @Override
    public void setLeftFinger(int finger) {

    }

    public long getHangtimeOpp() {
        return hangtimeOpp;
    }

    @Override
    public void setPullups(int pulls) {
        pullups = pulls;

        TVpullUpsYou.setText(String.valueOf(pulls));
    }

    @Override
    public void setHangtime(long time) {
        hangtime = time;

        TVhangtimeYou.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
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


    private void ProcessAndDisplayMessage(String message) {
      int code = Integer.valueOf(message);
        if (code > 200) {
            setRightGrabbedOpp(true);
            setRightGrabOpp((code - 200) / 10);
            setRightFinger(code % 10);
            setHangingOpp(true);
            setPullupsOpp(0);
            return;
        }
        if (code == 200) {
            setRightGrabbedOpp(false);
            setRightGrabOpp(0);
            setRightFinger(0);
            setHangingOpp(false);
            setPullupOpp(false);
            return;
        }if (code > 100) {
            setLeftGrabbedOpp(true);
            setLeftGrabOpp((code - 100) / 10);
            setLeftFinger(code % 10);
            setHangingOpp(true);
            setPullupsOpp(0);
            return;
        }
        if (code == 100) {
            setLeftGrabbedOpp(false);
            setLeftGrabOpp(0);
            setLeftFinger(0);
            setHangingOpp(false);
            setPullupOpp(false);
            return;
        }
        if (code == 91) {
            setPullupOpp(true);
            return;
        }
        if (code == 90) {
            setPullupOpp(false);
            stepPullUpsOpp();
            return;
        }
    }


    public static String getUser(){
        String s= ETuser.getText().toString();
        return s;
    }

    @Override
    public void decodeMsg(byte[] buf) {
        super.decodeMsg(buf);
        int code = buf[0] & 0xFF;
        XmppService.sendMessage(TrainTogetherActivity.this, ETuser.getText() + Util.SUFFIX_CHAT, Message.Type.chat,String.valueOf(code));

        }


    public void setHangingOpp(boolean val) {
        if (val && rightGrabbedOpp && leftGrabbedOpp) {

            hangingOpp = true;
            setHangtimeOpp(0);
            startHangtimeOpp();
        } else {
            hangingOpp = false;
            stopHangtimeOpp();
        }

    }


    private void startHangtimeOpp() {
        stopOpp = false;
        hangtimeHandlerOpp.post(runnableOpp);
    }

    private void stopHangtimeOpp() {
        stopOpp = true;

    }


    public void setPullupOpp(boolean val) {

    }


    public void setRightGrabbedOpp(boolean grab) {
        rightGrabbedOpp = grab;

    }


    public void setLeftGrabbedOpp(boolean grab) {
        leftGrabbedOpp = grab;
    }


    public void setRightGrabOpp(int hold) {
        rightGrabOpp = hold;

        TVGROpp.setText(String.valueOf(hold));
    }


    public void setLeftGrabOpp(int hold) {
        leftGrabOpp = hold;

        TVGLOpp.setText(String.valueOf(hold));
    }


    public void setRightFingerOpp(int finger) {

    }


    public void setLeftFingerOpp(int finger) {

    }

    public void setPullupsOpp(int pulls) {
        pullOpp = pulls;

        TVpullUpsOpp.setText(String.valueOf(pulls));
    }


    public void setHangtimeOpp(long time) {
        hangtimeOpp = time;

        TVhangtimeOpp.setText(String.valueOf((float) (super.getHangtime() / 100) / 10) + "s");
    }


    public void stepPullUpsOpp() {
        if (!hangingOpp){
            return;
        }
            setPullupsOpp(pullOpp + 1);


    }
    }



