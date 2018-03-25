/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindDrawable;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AddTaskActivity extends AppCompatActivity {

    // Declare a member variable to keep track of a task's selected mPriority
    private int mPriority;
    private boolean valid = true;
    public static String input;
    public static ArrayList<String> arrayList = new ArrayList<>();
    @BindView(R.id.photoPickerButton)
    ImageButton photo;
    private static final int RC_PHOTO_PICKER = 2;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    public static boolean enter;
    public static int count;
    private ScheduleClient scheduleClient;
    private Uri uri = null;
    public static long setTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child(getString(R.string.images));
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }

                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select)), RC_PHOTO_PICKER);
            }
        });

        // Initialize to highest mPriority by default (mPriority = 1)
        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        mPriority = 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            uri = data.getData();
            Log.v(AddTaskActivity.class.getSimpleName(), uri.toString());
            StorageReference photoRef = mStorageRef.child(uri.getLastPathSegment());
            photoRef.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    @TargetApi(23)
    public void onClickAddTask(View view) {
        // Not yet implemented
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String text = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        input = text;
        if (input.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        DatePicker picker = (DatePicker) findViewById(R.id.date);
        TimePicker time = (TimePicker) findViewById(R.id.timepicker);

        int hour = time.getHour();
        int min = time.getMinute();

        StringBuilder builder = new StringBuilder();
        builder.append((picker.getMonth() + 1) + "/");//month is 0 based
        builder.append(picker.getDayOfMonth() + "/");
        builder.append(picker.getYear());
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        if (picker.getYear() < year) {
            Toast.makeText(getApplicationContext(), getString(R.string.year), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            if (picker.getYear()==year  && picker.getMonth() < month) {
                Toast.makeText(getApplicationContext(), getString(R.string.month), Toast.LENGTH_SHORT).show();
                valid = false;
            } else {
                if (picker.getMonth()<=month && picker.getDayOfMonth() < day) {
                    Toast.makeText(getApplicationContext(), getString(R.string.day), Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            }
        }

        if (picker.getDayOfMonth() <=day && cal.get(Calendar.HOUR_OF_DAY) > hour) {
            valid = false;
            Toast.makeText(getApplicationContext(), getString(R.string.time1), Toast.LENGTH_SHORT).show();
        } else {
            if (picker.getYear()==year && picker.getMonth()==month && picker.getDayOfMonth()==day && hour == cal.get(Calendar.HOUR_OF_DAY)) {
                if (min < cal.get(Calendar.MINUTE)) {
                    valid = false;
                    Toast.makeText(getApplicationContext(), getString(R.string.time1), Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (uri == null || uri.toString().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.choose), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        String date = builder.toString();
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        c.set(year, month, day, hour, min, 0);
        setTime = c.getTimeInMillis();
        StringBuilder builder1 = new StringBuilder();
        builder1.append(time.getHour() + ":");
        if (min < 10)
            builder1.append("0");
        builder1.append(time.getMinute());
        String scheduledtime = builder1.toString();

        Task task = null;
        if (valid) {
            try {
                Toast.makeText(this, getString(R.string.not), Toast.LENGTH_SHORT).show();
                scheduleClient.setAlarmForNotification(c);
                Cursor cursor = getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                while (cursor != null && cursor.moveToNext()) {
                    String tasks = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION));
                    arrayList.add(tasks);
                }
                if (cursor == null || cursor.getCount() == 0)
                    arrayList.add(input);
                count = cursor.getCount();

                enter = false;
                TodoService.startActionUpdateWidgets(getApplicationContext());
                enter = true;
                task = new Task(input, mPriority, date, uri.toString(), MainActivity.user.getUid(), scheduledtime);
            } catch (NullPointerException e) {
            }
            MainActivity.mReference.push().setValue(task);
            finish();
        }
        valid = true;

    }

    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onPrioritySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mPriority = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mPriority = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mPriority = 3;
        }
    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if (scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }

}
