package com.example.android.todolist;

/**
 * Created by rusha on 23-07-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;


public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    private ArrayList<String> ingre = new ArrayList<>();

    @Override
    public void onCreate() {
        ingre = TodoService.tinku;
    }

    @Override
    public void onDataSetChanged() {
        ingre = TodoService.tinku;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingre.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        mView.setTextViewText(android.R.id.text1, ingre.get(i));
        return mView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

