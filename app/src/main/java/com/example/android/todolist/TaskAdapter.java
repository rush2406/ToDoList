package com.example.android.todolist;

/**
 * Created by rusha on 21-07-2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    TextView taskDescriptionView;
    TextView priorityView;
    TextView dateText;
    ImageView imageView;
    Context mContext;
    TextView mTime;

    public TaskAdapter(Context context, int resource, List<Task> objects) {
        super(context, resource, objects);
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
        dateText = (TextView) convertView.findViewById(R.id.date);
        imageView = (ImageView) convertView.findViewById(R.id.pic);
        mTime = (TextView) convertView.findViewById(R.id.timetext);


        Task task = getItem(position);


        taskDescriptionView.setText(task.getTask());
        String priorityString = "" + task.getPriority();
        priorityView.setText(priorityString);
        dateText.setText(task.getDate());
        mTime.setText(task.getSetTime());
        imageView.setImageBitmap(getBitmapFromUri(Uri.parse(task.getPhotoUrl())));
        GradientDrawable priorityCircle = (GradientDrawable) priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(task.getPriority());
        priorityCircle.setColor(priorityColor);
        return convertView;
    }

    private Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = 100;
        int targetH = 100;

        InputStream input = null;
        try {
            input = getContext().getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            if (input != null)
                input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            if (input != null)
                input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(AddTaskActivity.class.getSimpleName(), "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(AddTaskActivity.class.getSimpleName(), "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException ioe) {

            }
        }
    }


    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }

}
