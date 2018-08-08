package com.example.jdocter.lendme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";
    public static final String intentAction = "com.parse.push.intent.RECEIVE";
    private NotificationManager notificationManager;
    private String transactionId;
    private String itemImageUrl;
    private String borrowerImageUrl;
    private String borrowerName;
    private String itemName;
    private NotificationTarget notificationTarget;
    private NotificationTarget notificationTarget2;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(intentAction)) {
            String channel = intent.getExtras().getString("com.parse.Channel");
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    String value = json.getString(key);
                    Log.d(TAG, "..." + key + " => " + value);
                    // Extract custom push data
                    if (key.equals("customdata")) {
                        // create a local notification
                        createNotification(context, json);
                    }
//                    else if (key.equals("launch")) {
//                        // Handle push notification by invoking activity directly
//                        launchSomeActivity(context, value);
//                    } else if (key.equals("broadcast")) {
//                        // OR trigger a broadcast to activity
//                        triggerBroadcastToActivity(context, value);
//                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
                Log.d(TAG, "JSON failed!");
            }
        }
    }

    public static final int NOTIFICATION_ID = 45;





    public void createNotification(Context context, JSONObject jsonObject) throws JSONException {
        initChannels(context);

        //Parse Json nested object
        String values = jsonObject.getString("customdata");
        JSONObject object = new JSONObject(values);
        //do what you want with JSONObject
        Log.d("createNotification", object.toString());
        transactionId = object.getString("transactionId");
        itemImageUrl=object.getString("itemImageUrl");
        borrowerImageUrl=object.getString("borrowerProfile");
        borrowerName=object.getString("borrowerName");
        itemName=object.getString("item");
        final String message = "wants to borrow you item: "+itemName;

        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(context.getPackageName(), R.layout.notification_large);
        notificationLayout.setImageViewResource(R.id.ivItem,R.mipmap.ic_launcher);
        notificationLayout.setImageViewResource(R.id.ivBorrower,R.mipmap.ic_launcher);
        notificationLayout.setTextViewText(R.id.tvBorrower,borrowerName);
        notificationLayout.setTextViewText(R.id.tvMessage,message);


        // First let's define the intent to trigger when notification is selected
        // Start out by creating a normal intent (in this case to open an activity)

        Intent intentP = new Intent(context, MainActivity.class);
        intentP.putExtra("frgToLoad","notification");
        Intent intentA = new Intent(context, MainActivity.class);
        intentA.putExtra("frgToLoad","notification");
        intentA.putExtra("response","accept");
        intentA.putExtra("transactionId",transactionId);
        Intent intentD = new Intent(context, MainActivity.class);
        intentD.putExtra("frgToLoad","notification");
        intentD.putExtra("response","decline");
        intentD.putExtra("transactionId",transactionId);
        // Next, let's turn this into a PendingIntent using
        //   public static PendingIntent getActivity(Context context, int requestCode,
        //       Intent intent, int flags)
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one

        PendingIntent pIntent = PendingIntent.getActivity(context, requestID, intentP, flags);
        PendingIntent aIntent = PendingIntent.getActivity(context, 3, intentA, flags);
        PendingIntent dIntent = PendingIntent.getActivity(context, 4, intentD, flags);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setContentIntent(pIntent)
                .addAction(R.drawable.notification_accept, "Accept", aIntent)
                .addAction(R.drawable.notification_decline, "Decline", dIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); //dismiss the notification after user taps on it, but clicking on the accept or decline option do not work
        final Notification notification = mBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);

        notificationTarget = new NotificationTarget(
                context,
                R.id.ivItem,
                notificationLayout,
                notification,
                NOTIFICATION_ID);
        Glide
                .with(context)
                .asBitmap()
                .load(itemImageUrl)
                .apply(new RequestOptions().override(110, 110))
                .apply(new RequestOptions().centerCrop())
                .into(notificationTarget);

        notificationTarget2 = new NotificationTarget(
                context,
                R.id.ivBorrower,
                notificationLayout,
                notification,
                NOTIFICATION_ID);
        Glide
                .with(context)
                .asBitmap()
                .load(borrowerImageUrl)
                .apply(new RequestOptions().override(110, 110))
                .apply(new RequestOptions().centerCrop())
                .into(notificationTarget2);


    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }

    // Handle push notification by invoking activity directly
    // See: http://guides.codepath.com/android/Using-Intents-to-Create-Flows
    private void launchSomeActivity(Context context, String datavalue) {
        Intent pupInt = new Intent(context, MainActivity.class);
        pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pupInt.putExtra("data", datavalue);
        context.getApplicationContext().startActivity(pupInt);
    }

    // Handle push notification by sending a local broadcast
    // to which the activity subscribes to
    // See: http://guides.codepath.com/android/Starting-Background-Services#communicating-with-a-broadcastreceiver
    private void triggerBroadcastToActivity(Context context, String datavalue) {
        Intent intent = new Intent(intentAction);
        intent.putExtra("data", datavalue);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
