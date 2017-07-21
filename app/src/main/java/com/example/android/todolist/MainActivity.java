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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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
    //private ImageButton photo;
    private ChildEventListener mChild;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mStateListener;
    /*private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private static final int RC_PHOTO_PICKER = 2;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the RecyclerView to its corresponding view
        mListView = (ListView) findViewById(R.id.recyclerViewTasks);
        //photo = (ImageButton)findViewById(R.id.photoPickerButton);
       /* photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });*/
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
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Sign in cancelled", Toast.LENGTH_SHORT).show();
            }
            /*else if(resultCode==RC_PHOTO_PICKER&&requestCode==RESULT_OK) {
                Uri selectedImageUri = data.getData();
                StorageReference photoRef = mStorageRef.child(selectedImageUri.getLastPathSegment());
                photoRef.putFile(selectedImageUri);

            }*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStateListener != null)
            mAuth.removeAuthStateListener(mStateListener);
        mAdapter.clear();
        DetachListener();
    }

    private void onSignedInInitialize() {
        AttachListener();
    }

    private void OnSignedOut() {

        mAdapter.clear();
        DetachListener();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mStateListener);
    }

    private void AttachListener() {
        if (mChild == null) {
            mChild = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Task task = dataSnapshot.getValue(Task.class);
                    mAdapter.add(task);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
