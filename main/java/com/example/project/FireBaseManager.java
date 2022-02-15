package com.example.project;

import android.content.Context;
import android.location.GnssAntennaInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FireBaseManager {
    public static FireBaseManager instance;
    private static final String TAG = "FirebaseManager";

    public static FireBaseManager getInstance() {
        if (instance == null) {
            instance = new FireBaseManager();
        }
        return instance;
    }

    private FireBaseManager() {
        this.database= FirebaseDatabase.getInstance();
        messageReference = this.database.getReference("messages");

    }

    FirebaseDatabase database;
    DatabaseReference messageReference;

//    public void addMessage(Context context , Message message){
//        messageReference.push().setValue(message);
//    }

    public void addMessage(Message message, FireBaseListener listener) {
        DatabaseReference ref = messageReference.push();
        message.uid = ref.getKey();
        ref.setValue(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onSuccess(message);
            } else {
                listener.onFailed(task.getException().getMessage());
            }

        });

    }

    // read the messages from the firebase

    public void loadMessages(FireBaseListener listener) {
        messageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Message> messages = new ArrayList<>();

                for (DataSnapshot snapshotMessage : snapshot.getChildren()) {
                    messages.add(snapshotMessage.getValue(Message.class));
                }

                listener.onSuccess(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailed(error.getMessage());
            }
        });
    }

    // listen to messages

    public void listenToMessagesChange(FireBaseListener listener) {
        messageReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listener.onSuccess(snapshot.getValue(Message.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e(TAG,"onChildChanged");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e(TAG,"onChildRemoved");

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e(TAG,"onChildMoved");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,error.getMessage());

            }
        });
    }

}
