package com.atinity.doct;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {


    ImageView save;
    CircleImageView setting_image;
    EditText setting_name, setting_status;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    Uri selectedImageUri;

    String email;

    static String SPName="PatOrDoc";
    static String keyName="P_or_D";
    SharedPreferences SP;
    static String P_or_D;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SP=getSharedPreferences(SPName,MODE_PRIVATE);
        P_or_D=SP.getString(keyName,null);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        setting_image = findViewById(R.id.setting_image);
        setting_name = findViewById(R.id.setting_name);
        setting_status = findViewById(R.id.setting_status);

        save = findViewById(R.id.save);

        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        // 1st => accessing objects of users from realtime_database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String status = Objects.requireNonNull(snapshot.child("status").getValue()).toString();
                String image = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                setting_name.setText(name);
                setting_status.setText(status);

                Picasso.get().load(image).into(setting_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // => to open gallery
        setting_image.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });


        save.setOnClickListener(view -> {

            String name = setting_name.getText().toString();
            String status = setting_status.getText().toString();


            if (selectedImageUri != null) {
                storageReference.putFile(selectedImageUri).addOnCompleteListener(task ->
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                            String finalImageUri = uri.toString();
                            Users users = new Users(name, email, finalImageUri, auth.getUid(), status,P_or_D);

                            reference.setValue(users).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    Toast.makeText(SettingActivity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SettingActivity.this, HomeActivity_5.class));
                                }
                                else {
                                    Toast.makeText(SettingActivity.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }));
            } else {

                // download previous image and pass it to realtime_database
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String finalImageUri = uri.toString();
                    Users users = new Users(name, email, finalImageUri, auth.getUid(), status,P_or_D);

                    reference.setValue(users).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Toast.makeText(SettingActivity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingActivity.this, HomeActivity_5.class));
                        }
                        else {
                            Toast.makeText(SettingActivity.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == 10) {
                if(data != null) {
                    selectedImageUri = data.getData(); // we pass it into storage
                    setting_image.setImageURI(selectedImageUri); // we pass into picasso
                }
            }
        }
}
