package com.example.android.todolist;

/**
 * Created by rusha on 21-07-2017.
 */

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

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    TextView taskDescriptionView;
    TextView priorityView;
    TextView dateText;
    Context mContext;

    public TaskAdapter(Context context, int resource, List<Task> objects){
        super(context,resource,objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.task_layout, parent, false);
        }

        taskDescriptionView = (TextView) convertView.findViewById(R.id.taskDescription);
        priorityView = (TextView) convertView.findViewById(R.id.priorityTextView);
        dateText = (TextView)convertView.findViewById(R.id.date);

        Task task = getItem(position);


        taskDescriptionView.setText(task.getTask());
        String priorityString = ""+task.getPriority();
        priorityView.setText(priorityString);
        dateText.setText(task.getDate());
        GradientDrawable priorityCircle = (GradientDrawable) priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(task.getPriority());
        priorityCircle.setColor(priorityColor);
        return convertView;
    }

    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch(priority) {
            case 1: priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2: priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3: priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default: break;
        }
        return priorityColor;
    }

}
