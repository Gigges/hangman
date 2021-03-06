package com.example.dell.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import org.jivesoftware.smack.packet.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import services.XmppService;


public class ChatActivity extends Activity {

    EditText editText_mail_id;
    EditText editText_chat_message;
    ListView listView_chat_messages;
    Button button_send_chat;
    List<ChatObject> chat_list;

    BroadcastReceiver recieve_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Log.d("pavan","in chat "+getIntent().getStringExtra("user_id"));
        Log.d("pavan","in chat server "+Util.SERVER);
        XmppService.setupAndConnect(ChatActivity.this, Util.SERVER, "",
                getIntent().getStringExtra("user_id"), Util.XMPP_PASSWORD);
        Log.d("pavan",String.valueOf(XmppService.isConnected()));

        editText_mail_id= (EditText) findViewById(R.id.editText_mail_id);
        editText_chat_message= (EditText) findViewById(R.id.editText_chat_message);
        listView_chat_messages= (ListView) findViewById(R.id.listView_chat_messages);
        button_send_chat= (Button) findViewById(R.id.button_send_chat);
        button_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {

            // send chat message to server
                String message=editText_chat_message.getText().toString();

                showChat("sent",message);

              //  new SendMessage().execute();

                XmppService.sendMessage(ChatActivity.this, editText_mail_id.getText().toString() + Util.SUFFIX_CHAT, Message.Type.chat, message);
                editText_chat_message.setText("");


            }
        });






    }

    private void showChat(String type, String message){

        if(chat_list==null || chat_list.size()==0){

            chat_list= new ArrayList<ChatObject>();
        }

        chat_list.add(new ChatObject(message,type));

        ChatAdabter chatAdabter=new ChatAdabter(ChatActivity.this,R.layout.chat_view,chat_list);

        listView_chat_messages.setAdapter(chatAdabter);
        //chatAdabter.notifyDataSetChanged();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class SendMessage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String url = Util.send_chat_url+"?email_id="+editText_mail_id.getText().toString()+"&message="+editText_chat_message.getText().toString();
            Log.i("pavan", "url" + url);

            OkHttpClient client_for_getMyFriends = new OkHttpClient();;

            String response = null;
            // String response=Utility.callhttpRequest(url);

            try {
                url = url.replace(" ", "%20");
                response = callOkHttpRequest(new URL(url),
                        client_for_getMyFriends);
                for (String subString : response.split("<script", 2)) {
                    response = subString;
                    break;
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //Toast.makeText(context,"response "+result,Toast.LENGTH_LONG).show();



        }


    }



    // Http request using OkHttpClient
    String callOkHttpRequest(URL url, OkHttpClient tempClient)
            throws IOException {

        HttpURLConnection connection = tempClient.open(url);

        connection.setConnectTimeout(40000);
        InputStream in = null;
        try {
            // Read the response.
            in = connection.getInputStream();
            byte[] response = readFully(in);
            return new String(response, "UTF-8");
        } finally {
            if (in != null)
                in.close();
        }
    }

    byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1;) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }






}
