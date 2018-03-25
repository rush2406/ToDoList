package com.example.android.todolist;

/**
 * Created by rusha on 22-07-2017.
 */

import android.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

/**
 * This service is started when an Alarm has been raised
 * <p>
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 *
 * @author paul.blundell
 */
public class NotifyService extends Service {

    /**
     * Class for clients to access
     */
    String text;
    public static int id;

    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    // Unique id to identify the notification.
    private static final int NOTIFICATION = 123;
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "com.blundell.tut.service.INTENT_NOTIFY";
    // The system notification manager

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = intent.getStringExtra("key");
        // If this service was started by out AlarmTask intent then we want to show our notification
        if (intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification();

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {
        // This is the 'title' of the notification
        // This is the icon to use on the notification
        int icon = R.drawable.ic_dialog_alert;
        // This is the scrolling text of the notification
        // What time to show on the notification
        long time = System.currentTimeMillis();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("task", text);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(icon)
                        .setAutoCancel(true)
                        .setContentTitle(getString(com.example.android.todolist.R.string.todo_reminder))
                        .setContentText(text)
                        .addAction(Complete(getApplicationContext(), intent))
                        .setWhen(AddTaskActivity.setTime);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        id = (int) System.currentTimeMillis();
        mNotificationManager.notify(id, mBuilder.build());


        // Stop the service when we are finished
        //stopSelf();
    }

    private NotificationCompat.Action Complete(Context context, Intent i) {
        // COMPLETED (12) Create an Intent to launch WaterReminderIntentService
        String value = i.getStringExtra("task");
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.putExtra("text", value);
        // COMPLETED (13) Set the action of the intent to designate you want to increment the water count
        // COMPLETED (14) Create a PendingIntent from the intent to launch WaterReminderIntentService
        intent.setAction(ReminderTasks.ACTION_DELETE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // COMPLETED (15) Create an Action for the user to tell us they've had a glass of water
        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_delete,
                getString(com.example.android.todolist.R.string.compl) ,
                pendingIntent);
        // COMPLETED (16) Return the action
        return action;
    }

}
