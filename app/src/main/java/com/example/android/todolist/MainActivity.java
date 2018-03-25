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

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int RC_SIGN_IN = 1;

    // Member variables for the adapter and RecyclerView
    private TaskAdapter mAdapter;
    private ListView mListView;
    private FirebaseDatabase mFireBaseDataBase;
    public static DatabaseReference mReference;
    private ChildEventListener mChild;
    private InterstitialAd mInterstitialAd;
    private boolean isConnected;
    private FirebaseAuth mAuth;
    private TextView Empty;
    public static FirebaseUser user;
    private FirebaseAuth.AuthStateListener mStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.collapse);
        layout.setTitle(getString(R.string.todo));
        // Set the RecyclerView to its corresponding view
        mListView = (ListView) findViewById(R.id.recyclerViewTasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            mFireBaseDataBase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mReference = mFireBaseDataBase.getReference().child("todolist");
            // mStorageRef = mStorage.getReference().child("images");

            // Set the layout for the RecyclerView to be a linear layout, which measures and
            // positions items within a RecyclerView into a linear list
            // Initialize the adapter and attach it to the RecyclerView
            final List<Task> taskList = new ArrayList<>();
            mAdapter = new TaskAdapter(this, R.layout.task_layout, taskList);
            mListView.setAdapter(mAdapter);

            FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create a new intent to start an AddTaskActivity

                    mInterstitialAd = new InterstitialAd(MainActivity.this);
                    mInterstitialAd.setAdUnitId(getString(R.string.adsid));
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            //if (bar != null)
                            //  bar.setVisibility(View.GONE);
                            mInterstitialAd.show();
                        }

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                            startActivity(addTaskIntent);
                        }

                        @Override
                        public void onAdClosed() {

                            Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                            startActivity(addTaskIntent);
                        }
                    });

                    AdRequest ar = new AdRequest
                            .Builder().build();
                    mInterstitialAd.loadAd(ar);
                }
            });

            mStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        onSignedInInitialize();
                    } else {
                        OnSignedOut();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setProviders(
                                                AuthUI.GOOGLE_PROVIDER,
                                                AuthUI.EMAIL_PROVIDER)
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };
        } else {
            Empty.setVisibility(View.VISIBLE);
            Empty.setText(getString(R.string.net));
            mListView.setVisibility(View.GONE);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isConnected) {
            if (mAuth.getCurrentUser() != null) {
                user = mAuth.getCurrentUser();
                Cursor cursor = getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                if (cursor.getCount() != 0 || cursor != null) {
                    getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, null, null);
                    if (!AddTaskActivity.enter)
                        for (int i = 0; i < TodoService.tinku.size(); i++)
                            TodoService.tinku.remove(i);
                }
                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                user = mAuth.getCurrentUser();
                Toast.makeText(getApplicationContext(), getString(R.string.sign) + " " + user.getEmail(), Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isConnected) {
            if (mStateListener != null)
                mAuth.removeAuthStateListener(mStateListener);
            mAdapter.clear();
            DetachListener();
        }
    }

    private void onSignedInInitialize() {

        if (mAuth.getCurrentUser() != null)
            AttachListener();
    }

    private void OnSignedOut() {
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        mAdapter.clear();
        DetachListener();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected)
            mAuth.addAuthStateListener(mStateListener);
    }

    private void AttachListener() {
        if (mChild == null) {
            mChild = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (user.getUid().equals(task.getId())) {
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.getTask());
                        getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);
                        for (int i = 0; i < TodoService.tinku.size(); i++)
                            TodoService.tinku.remove(i);
                        mAdapter.add(task);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mReference.addChildEventListener(mChild);
        }
    }

    private void DetachListener() {
        if (mChild != null) {
            mReference.removeEventListener(mChild);
            mChild = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sign_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                AuthUI.getInstance().signOut(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
