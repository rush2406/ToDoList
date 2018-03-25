package com.example.android.todolist;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rusha on 23-07-2017.
 */

public class ReminderTasks {
    public static final String ACTION_DELETE = "delete";
    //  COMPLETED (2) Add a public static constant called ACTION_DISMISS_NOTIFICATION

    public static void executeTask(final Context context, String action, String value) {
        if (ACTION_DELETE.equals(action)) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child("todolist").orderByChild("task").equalTo(value);

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancel(NotifyService.id);
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(ReminderTasks.class.getSimpleName(), "onCancelled", databaseError.toException());
                }
            });


        }
    }
}
