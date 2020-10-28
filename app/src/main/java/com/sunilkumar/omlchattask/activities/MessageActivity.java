package com.sunilkumar.omlchattask.activities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sunilkumar.omlchattask.adapters.MessageAdapter;
import com.sunilkumar.omlchattask.R;
import com.sunilkumar.omlchattask.databinding.ActivityMessageBinding;
import com.sunilkumar.omlchattask.form_verification.VerifyUserDetails;
import com.sunilkumar.omlchattask.models.Chat;
import com.sunilkumar.omlchattask.models.Users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener1, valueEventListener2;

    Intent intent;
    RecyclerView recyclerViewMessage;

    MessageAdapter messageAdapter;
    List<Chat> chatList;
    String userid;

    ActivityMessageBinding messageBinding;

    // attachment uploading and sending
    private Uri uriUpload;
    StorageReference storageReference;
    StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sound to play when send button is clicked.
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound);

        // // attachment uploading and sending
        storageReference = FirebaseStorage.getInstance().getReference("sent");

        //binding
        messageBinding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(messageBinding.getRoot());

        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbarIdMessageActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // will hide the toolbar title n disply user's name
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // back arrow on toolbar will be visible
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {    // clicking on arrow will finish activity
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // setting up recyclerview
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);

        // fetching both users
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert userid != null;

        // getting db reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                messageBinding.textViewMessageActivity.setText(user.getUsername());

                if(user.getImageURL().equals("default"))
                {
                    messageBinding.imageViewMessageActivity.setImageResource(R.drawable.ic_launcher_background);
                } else if(!MessageActivity.this.isFinishing())
                {
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURL())
                            .into(messageBinding.imageViewMessageActivity);
                }

                // online status of user
                if(user.getStatus().equals("Online"))
                {
                    messageBinding.userStatus.setText("Online");
                }

                readMessage(firebaseUser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener1);

        // Typing status implementation
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = dbRef.orderByChild("id").equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String typingStatus = ""+ds.child("typingStatus").getValue();
                    String onlineStatus = ""+ds.child("status").getValue();

                    if(typingStatus.equals(firebaseUser.getUid()))
                    {
                        messageBinding.userStatus.setText("Typing...");
                    }else if(onlineStatus.equalsIgnoreCase("Online"))
                    {
                        messageBinding.userStatus.setText("Online");
                    }else
                    {
                        messageBinding.userStatus.setText("Offline");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // send button behavior.. if msg empty & not empty..change button appearance
        messageBinding.buttonSendMessage.setEnabled(false);

        messageBinding.textInputLayout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                messageBinding.buttonSendMessage.setEnabled(charSequence.length() >= 1);

                Toast.makeText(MessageActivity.this, ""+charSequence.toString().length(), Toast.LENGTH_SHORT).show();
                if(charSequence.toString().trim().length() == 0)
                {
                    checkTypingStatus("onOne");
                }else
                {
                    checkTypingStatus(userid);   // here we update, the receiver is typing
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        messageBinding.buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textMsg = messageBinding.textInputLayout.getText().toString().trim();
                if(!textMsg.equals(""))
                {
                    sendMessage(firebaseUser.getUid(), userid, textMsg);
                    messageBinding.textInputLayout.setText("");

                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.start();
                } else
                {
                    Toast.makeText(MessageActivity.this, "Can't send empty msg!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // For sending message.
    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        databaseReference.child("Chats").push().setValue(hashMap);

        final DatabaseReference databaseChatReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    databaseChatReference.child("id").setValue(userid);
                } }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        databaseChatReference.addListenerForSingleValueEvent(valueEventListener2);


        final DatabaseReference databaseUserReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userid)
                .child(firebaseUser.getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    databaseUserReference.child("id").setValue(firebaseUser.getUid());
                } }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseUserReference.addValueEventListener(valueEventListener);
    }

    // For reading message.
    private void readMessage(final String myid, final String userid, final String imgURL)
    {
        chatList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid))
                    {
                        chatList.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chatList, imgURL);
                    recyclerViewMessage.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // check if user is online/offline
    public void checkStatus(String status)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }

    // check if user is typing
    public void checkTypingStatus(String typing)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingStatus", typing);
        databaseReference.updateChildren(hashMap);
    }


    // update if user is online
    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("Online");
    }

    // update if user is offline
    @Override
    protected void onPause() {
        super.onPause();
        checkStatus("Offline");
        checkTypingStatus("onOne");
    }

    // we neet to remove refrence to db when exiting
    @Override
    protected void onStop() {
        super.onStop();
        if(valueEventListener1!=null)
            databaseReference.removeEventListener(valueEventListener1);
        if(valueEventListener2!=null)
            databaseReference.removeEventListener(valueEventListener2);
    }

}