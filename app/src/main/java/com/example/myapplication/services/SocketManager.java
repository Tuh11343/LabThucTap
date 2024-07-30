package com.example.myapplication.services;

import android.util.Log;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;

public class SocketManager {
    private static final String TAG = "SocketManager";
    private static final String SOCKET_URL = "http://192.168.1.13:5000";
    private static Socket mSocket;

    public static Socket getmSocket() {
        return mSocket;
    }

    static {
        try {
            mSocket = IO.socket(SOCKET_URL);
        } catch (URISyntaxException e) {
            Log.e(TAG, "URISyntaxException: " + e.getReason());
        }
    }

    public static void connect() {

//        IO.Options options = new IO.Options();
//        options.transports = new String[]{WebSocket.NAME, Polling.NAME};
        try{
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Socket connected");
                }
            });
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Socket disconnected");
                }
            });
            mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                Exception e = (Exception) args[0];
                Log.d(TAG, "Socket connect error:"+e);
            });
            mSocket.on("message", onNewMessage);
        }catch (Exception e){
            Log.e(TAG,"Error from connect:"+e);
        }
    }

    public static void disconnect() {
        mSocket.disconnect();
    }

    private static Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String message = (String) args[0];
            Log.d(TAG, "New message received: " + message);
            // Handle the new message
        }
    };

    public static void sendMessage(String message) {
        mSocket.emit("message", message);
    }
}
