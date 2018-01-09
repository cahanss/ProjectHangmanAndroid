package com.example.hangmanAndroid.net;

/**
 * Created by camilla on 2018-01-05.
 */

    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


    public class ServerConnection implements Runnable {

        private static final int PORT = 3334;
        private static final String HOST_ADDRESS = "10.0.2.2";
        private static final int TIMEOUT = 150000;
        private Socket socket;
        private DataOutputStream toServer;
        private DataInputStream fromServer;
        private boolean connected;

        public void connect() {
            try {
                this.socket = new Socket(HOST_ADDRESS, PORT);
                this.socket.setSoTimeout(TIMEOUT);
                this.fromServer = new DataInputStream(socket.getInputStream());
                this.toServer = new DataOutputStream(socket.getOutputStream());
                this.connected = true;

            } catch (IOException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        public void send(String input) {
            try {
                this.toServer.writeUTF(input);
                this.toServer.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void createListener(Handler handler){

            new Thread(new Listener(handler)).start();
        }

        public void run() {
        }

        private class Listener implements Runnable {

            private final Handler toClient;

            private Listener(Handler toClient) {
                this.toClient = toClient;
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        String msgFromServer = (String) fromServer.readUTF();
                        Bundle b = new Bundle();
                        Message message = new Message();
                        b.putString("KEY", msgFromServer);
                        message.setData(b);
                        toClient.handleMessage(message);
                    }
                } catch (IOException ex) {
                    if (connected) {
                        Bundle b = new Bundle();
                        Message message = new Message();
                        b.putString("KEY", "Connection lost");
                        message.setData(b);
                        toClient.handleMessage(message);
                    }
                }
            }
        }
    }
