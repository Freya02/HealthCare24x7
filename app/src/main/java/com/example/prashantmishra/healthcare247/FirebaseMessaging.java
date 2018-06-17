package com.example.prashantmishra.healthcare247;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {
String title,body;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        title=remoteMessage.getNotification().getTitle();
        body=remoteMessage.getNotification().getBody();
        NotificationManager.getInstance(getApplicationContext()).displayNotification(title, body);

    }
}
