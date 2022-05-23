package com.atinity.doct;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity_3 extends AppCompatActivity {

    TextView txt_sign_in, btn_signUp;
    CircleImageView profile_image;
    EditText reg_name, reg_email, reg_pass, reg_cPass;

    public static String doc;

    FirebaseAuth auth;
    Uri imageUri;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseDatabase database;
    FirebaseStorage storage;

    // store the uri/url value of image that we get from firebase storage when we store that image
    String imageURI;

    //
    static String SPName="PatOrDoc";
    static String keyName="P_or_D";
    SharedPreferences SP;
    String P_or_D;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_3);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SP=getSharedPreferences(SPName,MODE_PRIVATE);//Initializing shared preferences in private mode then we will read data stored in it
        P_or_D=SP.getString(keyName,null);// Reading data from Shared Preferences
        Log.d("Button Clicked Was",P_or_D);


        // 1st => we get all the values, objects / instance of storage and realtime database

        txt_sign_in = findViewById(R.id.txt_signin);
        btn_signUp = findViewById(R.id.btn_signUp);

        reg_name = findViewById(R.id.reg_name);
        reg_email = findViewById(R.id.reg_email);
        reg_pass = findViewById(R.id.reg_pass);
        reg_cPass = findViewById(R.id.reg_cPass);
        profile_image = findViewById(R.id.profile_image);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();



        // 2nd => its the main functions that checks, when user press the sign up btn,
        // all the inputs from user are valid or not
        btn_signUp.setOnClickListener(view -> {

            String name = reg_name.getText().toString();
            String email = reg_email.getText().toString();
            String password = reg_pass.getText().toString();
            String cPassword = reg_cPass.getText().toString();
            String status;
            if (P_or_D.equals("patient")) {
                status = "I am a patient";
            }
            else if (P_or_D.equals("doctor"))
            {
                status = "I am a doctor";
            }
            else
            {
                status="I am nothing";
            }

            // checking validation
            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)) {
                Toast.makeText(SignupActivity_3.this, "Enter valid Data", Toast.LENGTH_SHORT).show();
            } else if(!email.matches(emailPattern)) {
                reg_email.setError("Invalid Email");
                Toast.makeText(SignupActivity_3.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            } else if(password.length() < 6 || !password.equals(cPassword)) {
                reg_pass.setError("Invalid Password");
                Toast.makeText(SignupActivity_3.this, "Password is too short or\n Password not Matched", Toast.LENGTH_SHORT).show();
            } else {

                // 3rd => to create user in firebase
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                    // 3.1 => if user created successfully
                    if(task.isSuccessful()) {

                        // 3.1.1 => here, references of both realtime_database and storage act as the pointers
                        // these pointers point at the specific location, in which we want to add data
                        // or point at the specific location, from where we want to access data in firebase database
                        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());


                        // 3.2 => in our app, we give option to users, whether he/she wants to set his/her
                        // image. User can also choose to set his picture later.



                        // 3.3 => If user selects the image, then first we store its image in storage, through storage reference pointer.
                        // but before moving further in code, you can understand the way to get the image from phone. So
                        // for this purpose move to 5th index, or you can check it later
                        if(imageUri != null) {

                            storageReference.putFile(imageUri).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    // 3.3.1 => if image is stored successfully, then we get the URL(String) of image from
                                    // storage and use that URL(String) to save image on realtime_database
                                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                                        imageURI = uri.toString();

                                        // 3.5 => why we need model class to save data into firebase?

                                        // Rule By Firebase.
                                        // reason => Let, we are storing data. As each user have a unique id or something,
                                        // it makes sense to use the set method here instead of the push method since you already
                                        // have the key and don't need to create one.

                                        Users users = new Users(name, email, imageURI, auth.getUid(), status,P_or_D);

                                        // 3.6 => setting all values into realtime_database
                                        reference.setValue(users).addOnCompleteListener(task11 -> {

                                            if(task11.isSuccessful()) {
                                                Intent i = new Intent(SignupActivity_3.this, HomeActivity_5.class);
                                                startActivity(i);
                                                finish();
                                            } else { Toast.makeText(SignupActivity_3.this, "Error in Creating User", Toast.LENGTH_SHORT).show(); }
                                        });
                                    });
                                }
                            });
                        } else {
                            // 3.4 => user didn't select image
                            imageURI = "https://firebasestorage.googleapis.com/v0/b/doct-8e8d5.appspot.com/o/female_doctor_avatar.png?alt=media&token=40d3bf94-5ea7-4ad0-b782-26e39f7f2c5e";

                            // 3.5 => (again)
                            Users users = new Users(name, email, imageURI, auth.getUid(), status,P_or_D);

                            // 3.6 => (again)
                            reference.setValue(users).addOnCompleteListener(task12 -> {
                                if(task12.isSuccessful()) {

                                    Intent i = new Intent(SignupActivity_3.this, HomeActivity_5.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    Toast.makeText(SignupActivity_3.this, "Error in Creating User", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        // if user not created successfully
                        Toast.makeText(SignupActivity_3.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // 4th => code to open gallery from phone
        profile_image.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            // here => we have request code (10)
            // The request code identifies the return result when the result arrives.
            // When results arrive, you use the request code to distinguish one result from another.
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });

        txt_sign_in.setOnClickListener(view -> {
            Intent i = new Intent(SignupActivity_3.this, LoginActivity_4.class);
            startActivity(i);
        });
    }

    // Learn more about request code in
    // https://www.dummies.com/article/technology/programming-web-design/app-development/how-to-get-results-back-from-an-activity-in-your-android-app-140885/
    // https://stackoverflow.com/questions/9268153/what-is-the-meaning-of-requestcode-in-startactivityforresult



    // 5th => selected image save into Uri object

    // uri is a sequence of characters used for identification of a particular resource.
    // Uri object is usually used to tell a ContentProvider what we want to access by reference.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10) {
            if(data != null) {

                imageUri = data.getData();

                profile_image.setImageURI(imageUri);
            }
        }
    }
}

