package com.example.android.todolist;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by rusha on 23-07-2017.
 */

public class ReminderIntentService extends IntentService {

    public ReminderIntentService() {
        super("ReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        String value = intent.getStringExtra("text");
        ReminderTasks.executeTask(this, action, value);
    }
}
