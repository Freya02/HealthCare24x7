package com.example.prashantmishra.healthcare247;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationManager {
    private Context ctx;
    private  static NotificationManager myNotificationManager;
    private NotificationManager(Context context)
    {
        this.ctx=context;
    }
    public static synchronized NotificationManager getInstance(Context context){
        if(myNotificationManager==null){
            myNotificationManager=new NotificationManager(context);
        }
        return myNotificationManager;
    }

    public void displayNotification(String title,String body){
        Log.d("freya123",title+" "+body);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(ctx,"mychannelid")
                .setSmallIcon(R.drawable.mapbox_logo_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false);
        Intent intent=new Intent(ctx,LoginActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(ctx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        //NotificationManager notificationManager= (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if(myNotificationManager!=null) {
            myNotificationManager.notify();
        }

    }
}
