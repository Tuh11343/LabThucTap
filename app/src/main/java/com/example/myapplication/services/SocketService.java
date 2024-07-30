package com.example.myapplication.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {

    public static String CHANNEL_ID="SOCKET_CHANNEL";
    private final String TAG="SocketService";

    private Socket mSocket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try{
            mSocket=SocketManager.getmSocket();
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
            mSocket.on("response", onNewMessage);
        }catch (Exception e){
            Log.e(TAG,"Error from connect:"+e);
        }

        //Xử lý quá trình tạo thông báo
        createNotificationChannel();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }



    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String message = (String) args[0];
            Log.d(TAG, "New message received: " + message);

            // Xử lý tạo thông báo khi nhận tin nhắn từ server
            showNotification(message);
        }
    };

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Socket Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Push Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationManager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tieu de")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(false)
                .build();

        notificationManager.notify(2,notification);
    }
}
