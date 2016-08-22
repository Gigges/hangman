package com.example.dell.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by johan on 06.07.2016.
 */
public class ConnectBtActivity extends ActionBarActivity {

    public static Boolean OFFLINE = false;

    static BluetoothChatFragment bluetoothChatFragment = null;

    Button button_textInterface;
    Button button_graphInterface;
    Button button_train_together;
    Button button_training;
    Button button_coop;
    public static TextView textView;
    private boolean mLogShown;
    BroadcastReceiver recieve_chat;

    public static boolean btconnected = false;

    BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_connection);

        //connect to XMPP server
        Log.d("pavan","in chat "+getIntent().getStringExtra("user_id"));
        Log.d("pavan","in chat server "+Util.SERVER);



        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            bluetoothChatFragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, bluetoothChatFragment);
            transaction.commit();
        }

        textView = (TextView) findViewById(R.id.textView);
        button_textInterface =(Button) findViewById(R.id.button_textInterface);
        button_textInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btconnected) {
                    TextInterface textInterface = new TextInterface();
                    Intent myIntent = new Intent(ConnectBtActivity.this, textInterface.getClass());
                    startActivity(myIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Connect to a Board first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_graphInterface =(Button) findViewById(R.id.button_graphInterface);
        button_graphInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btconnected) {
                    GraphInterface graphInterface = new GraphInterface();
                    Intent myIntent=new Intent(ConnectBtActivity.this, graphInterface.getClass());
                    startActivity(myIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Connect to a Board first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_train_together=(Button)findViewById(R.id.buttonTogether);
        button_train_together.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String uid = getIntent().getStringExtra("user_id");
                Intent myIntent=new Intent(ConnectBtActivity.this,TrainTogetherActivity.class);
                myIntent.putExtra("user_id",uid);
                startActivity(myIntent);
            }
        });

        button_coop=(Button)findViewById(R.id.button_coop);
        button_coop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String uid = getIntent().getStringExtra("user_id");
                Intent myIntent=new Intent(ConnectBtActivity.this, CoopActivity.class);
                myIntent.putExtra("user_id",uid);
                startActivity(myIntent);
            }
        });

        button_training =(Button) findViewById(R.id.button_training);
        button_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btconnected) {
                    TrainingActivity trainingActivity = new TrainingActivity();
                    Intent myIntent = new Intent(ConnectBtActivity.this, trainingActivity.getClass());
                    startActivity(myIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Connect to a Board first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (OFFLINE) {
            button_train_together.setClickable(false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? "Hide Log" : "show log");

        return super.onPrepareOptionsMenu(menu);
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
            String url = Util.send_chat_url+"?email_id="+"klaus"+"&message="+"hi";
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

