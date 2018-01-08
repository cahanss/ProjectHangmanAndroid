package com.example.hangmanAndroid.startUp;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hangmanAndroid.R;
import com.example.hangmanAndroid.net.ServerConnection;

public class MainActivity extends AppCompatActivity {
    Handler clientHandler;
    TextView textView;
    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        textView = (TextView) findViewById(R.id.textView);
        clientHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle b = msg.getData();
                        String key = b.getString("KEY");
                        System.out.println(key);
                        textView.setText(key);
                    }
                });
            }
        };
        new ConnectServer().execute();
    }

    public void sendMessage(View view){
        EditText editText = (EditText) findViewById(R.id.instruction);
        String message = editText.getText().toString();
        editText.setText(null);
        new SendToServer().execute(message);
    }

    private class ConnectServer extends AsyncTask<Void, Void, ServerConnection> {

        @Override
        protected ServerConnection doInBackground(Void...voids ) {
            ServerConnection serverConnection = new ServerConnection();
            serverConnection.connect();
            serverConnection.createListener(clientHandler);
            return serverConnection;
        }



        @Override
        protected void onPostExecute(ServerConnection serverConnection){
            MainActivity.this.serverConnection = serverConnection;
        }
    }

    private class SendToServer extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            serverConnection.send(params[0]);
            return null;
        }
    }
}
