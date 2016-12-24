package com.sanislo.lostandfound;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.utils.FirebaseConstants;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 24.12.16.
 */

public class AddThingActivity extends AppCompatActivity {
    public static final String TAG = AddThingActivity.class.getSimpleName();

    @BindView(R.id.sp_category)
    Spinner spCategory;

    @BindView(R.id.edt_thing_title)
    EditText edtTitle;

    @BindView(R.id.edt_thing_description)
    EditText edtDescription;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thing);
        ButterKnife.bind(this);
        initFirebase();
        getCategories();
    }

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    private void getCategories() {
        mDatabaseReference.child(FirebaseConstants.CATEGORIES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot);
                        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @OnClick(R.id.btn_add_thing)
    public void onClickAddThing() {
        addThing();
    }

    private void addThing() {
        String thingKey = mDatabaseReference
                .child(FirebaseConstants.THINGS)
                .push()
                .getKey();
        long timestamp = new Date().getTime();
        Thing.Builder thingBuilder = new Thing.Builder()
                .setKey(thingKey)
                .setTitle(edtTitle.getText().toString())
                .setDescription(edtDescription.getText().toString())
                .setTimestamp(timestamp);
        Thing thing = thingBuilder.build();
        Log.d(TAG, "addThing: " + thing);
        Log.d(TAG, "addThing: " + thingKey);
        DatabaseReference newThingReference = mDatabaseReference
                .child(FirebaseConstants.THINGS)
                .child(thingKey);
        Log.d(TAG, "addThing: " + newThingReference.toString());
        newThingReference.setValue(thing)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
