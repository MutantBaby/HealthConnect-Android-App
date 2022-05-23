package com.atinity.doct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private Parcelable recyclerViewState;

    String ReceiverImage, ReceiverUID, ReceiverName, senderUID;

    CircleImageView profileImage;
    TextView receiverName;

    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;

    public static String sImage;
    public static String rImage;

    CardView sendBtn;
    EditText editMessage;

    String senderRoom, receiverRoom;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList = new ArrayList<>();

    MessagesAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // data of user on which we click, getting from UserAdapter intent.
        ReceiverName = getIntent().getStringExtra("name");
        ReceiverImage = getIntent().getStringExtra("receiverImage");
        ReceiverUID = getIntent().getStringExtra("uid");
        senderUID = firebaseAuth.getUid();

        profileImage = findViewById(R.id.profile_image);
        receiverName = findViewById(R.id.receiverName);
        // setting image
        Picasso.get().load(ReceiverImage).into(profileImage);

        receiverName.setText("" + ReceiverName);

        sendBtn = findViewById(R.id.sendBtn);
        editMessage = findViewById(R.id.editMessage);

        senderRoom = senderUID + ReceiverUID;
        receiverRoom = ReceiverUID + senderUID;

        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // layout work in reverse order

        messageAdapter.setLayoutManager(linearLayoutManager);

        adapter = new MessagesAdapter(ChatActivity.this, messagesArrayList);
        adapter.notifyDataSetChanged();

        messageAdapter.scrollToPosition(messagesArrayList.size() - 1);
        messageAdapter.setAdapter(adapter);

        // 1st
        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        // 2nd
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        // 1.1 => getting image from database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                rImage = ReceiverImage;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // 2.1
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }

                adapter.notifyDataSetChanged();
                linearLayoutManager.getReverseLayout();
                messageAdapter.scrollToPosition(messagesArrayList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        sendBtn.setOnClickListener(view -> {
            String message = editMessage.getText().toString();
            if(message.isEmpty()) {
                        Toast.makeText(ChatActivity.this, "Enter some message", Toast.LENGTH_SHORT).show();
                return;
            }

            editMessage.setText("");
            Date date = new Date();

            Messages messages = new Messages(message, senderUID, date.getTime());

            database.getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(messages)
                    .addOnCompleteListener(task ->
                            database.getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(task1 -> {}));
        });
    }
}