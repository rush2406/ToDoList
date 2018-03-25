package com.example.android.todolist;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by rusha on 23-07-2017.
 */

public class TodoService extends IntentService {

    public static ArrayList<String> tinku = new ArrayList<>();

    public static final String ACTION_UPDATE_WIDGETS = "com.example.android.todolist.action.update_widgets";

    public TodoService() {
        super("TodoService");
    }

    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, TodoService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.getAction().equals(ACTION_UPDATE_WIDGETS))
            handleActionUpdateWidgets();

    }

    private void handleActionUpdateWidgets() {

        Cursor cursor = getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (AddTaskActivity.enter && tinku.size() == 3) {
            for (int i = 0; i < tinku.size(); i++)
                tinku.remove(i);
        }

        while (cursor != null && cursor.moveToNext()) {
            String value = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION));
            Log.v(TodoService.class.getSimpleName(), "Hello");
            tinku.add(value);
        }
        cursor.close();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TodoWidgetProvider.class));

        TodoWidgetProvider.updateWidgets(this, appWidgetManager, appWidgetIds);
    }

}
