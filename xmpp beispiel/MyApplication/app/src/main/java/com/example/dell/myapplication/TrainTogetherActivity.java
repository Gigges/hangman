package com.example.dell.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
    TextView TVhantimeOpp;
   public static EditText ETuser;
    OurMsg message;
    Connection connection;


    BroadcastReceiver recieve_chat;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectBtActivity.bluetoothChatFragment.setReceiver(this);

        connection = XmppService.getConnection();


        setContentView(R.layout.activity_train_together);

        TVpullUpsYou=(TextView)findViewById(R.id.textViewPullupsYou);
        TVpullUpsOpp=(TextView)findViewById(R.id.textViewPullupsOpponent);

        TVGLYou=(TextView)findViewById(R.id.textViewGLyou);
        TVGLOpp=(TextView)findViewById(R.id.textViewGrabbedLeftOpp);

        TVGRYou=(TextView)findViewById(R.id.textViewGRyou);
        TVGROpp=(TextView)findViewById(R.id.textViewGrabbedRightOpp);

        TVhangtimeYou=(TextView)findViewById(R.id.textViewHangimteYou);
        TVhantimeOpp=(TextView)findViewById(R.id.textViewHangtimeIOpp);

        ETuser=(EditText)findViewById(R.id.editTextOpp);

        message= new OurMsg(0,0,0,0);


        recieve_chat=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String message=intent.getStringExtra("message");

                Log.d("pavan","in local braod "+message);
               processMessage(message);


            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(recieve_chat, new IntentFilter("message_recieved"));




    }

    @Override
    public void clearView() {

    }

    @Override
    public void onHangtimeChange() {
        long hangtime = super.getHangtime();
        XmppService.sendMessage(TrainTogetherActivity.this,ETuser.getText()+Util.SUFFIX_CHAT, Message.Type.chat,message.toString());
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
        message.setGrabbed_right(hold);
        TVGRYou.setText(String.valueOf(hold));
    }

    @Override
    public void setLeftGrab(int hold) {
        leftGrab = hold;
        message.setGrabbed_left(hold);
        TVGLYou.setText(String.valueOf(hold));
    }

    @Override
    public void setRightFinger(int finger) {

    }

    @Override
    public void setLeftFinger(int finger) {

    }

    @Override
    public void setPullups(int pulls) {
        pullups = pulls;
        message.setPullups(pulls);
        TVpullUpsYou.setText(String.valueOf(pulls));
    }

    @Override
    public void setHangtime(long time) {
        hangtime = time;
        message.setHangtime(hangtime);
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


    private void processMessage(String message) {
        String s = message.substring(1, message.length()-1);
        String[] array = s.split(",");
        TVpullUpsOpp.setText(Integer.valueOf(array[0]));
        TVhantimeOpp.setText(Integer.valueOf(array[1]));
        TVGLOpp.setText(Integer.valueOf(array[2]));
        TVGRYou.setText(Integer.valueOf(array[3]));
    }

    public static String getUser(){
        String s= ETuser.getText().toString();
        return s;
    }
}
