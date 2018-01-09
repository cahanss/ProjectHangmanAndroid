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
    Handler clientHandler;                                                      // Creates a Handler, used to post runnable from backround thread
                                                                // on GUI thread (runOnUiThread)
    TextView textView;                                                          // Creates a TextView which is displayed in the GUI
    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {                        // Overrides the onCreate which is run as the application is
                                                                // created. Is starts all things in the background needed to start the app.
        super.onCreate(savedInstanceState);                                     // runs the things in the generic onCreate, if not used onCreate
                                                                // will not run. super runs a set of methods on create that we dont want to have
                                                                // to worry about.
        setContentView(R.layout.activity_main);                 // get from resources, activity_main som är appens utseende

    }

    @Override
    protected void onStart() {                                                  // always called after onCreate Is whats run as the acctual
                                                                                // "opening" of the app in the GUI happens.
        super.onStart();
        textView = (TextView) findViewById(R.id.textView);                      // sätt textView till resourcen textView
        clientHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                runOnUiThread(new Runnable() {                                  // run on main thread a new Runnable
                    @Override
                    public void run() {
                        Bundle b = msg.getData();
                        String key = b.getString("KEY");                    // the string stored with the key "key"
                        textView.setText(key);                                  // sets textVeiw to the text received.
                    }
                });
            }
        };
        new ConnectServer().execute();                                          // kör metoden doInBackground i ConnectServer
    }

    public void sendMessage(View view){                                         // Is excecuted when you push button.
        EditText editText = (EditText) findViewById(R.id.instruction);          // sets sets editText to the contents of what player has written
        String message = editText.getText().toString();                         // gets message from editText
        editText.setText(null);                                                 // sets it to null
        new SendToServer().execute(message);                                    // calls SendToServer to bbe excecuted with the new msg
    }

    private class ConnectServer extends AsyncTask<Void, Void, ServerConnection> { //first param = in-param, mitten param = progress status
                                                                            // last param = return-param
        @Override
        protected ServerConnection doInBackground(Void...voids ) {                // doInBackground kallas på när excecute körs. void är den första paramen
                                                                                  // från AsyncTask.
            ServerConnection serverConnection = new ServerConnection();
            serverConnection.connect();
            serverConnection.createListener(clientHandler);
            return serverConnection;                                              // returns serverConnection to next step in async task which is onPostExcecute
        }



        @Override
        protected void onPostExecute(ServerConnection serverConnection){           //is called after the connectServer is run.
            MainActivity.this.serverConnection = serverConnection;
        }
    }

    private class SendToServer extends AsyncTask<String, Void, Void> {              //AsyncTask first param is in-param
        @Override
        protected Void doInBackground(String... params) {                           // doInBackground kallas på när excecute körs. params är den tredje paramen
            serverConnection.send(params[0]);                                       // calls send with the msg to be printed
            return null;
        }
    }
}