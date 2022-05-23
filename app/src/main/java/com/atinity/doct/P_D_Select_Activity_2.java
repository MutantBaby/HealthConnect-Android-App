package com.atinity.doct;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class P_D_Select_Activity_2 extends AppCompatActivity {

    FirebaseAuth auth;
    static String SPName="PatOrDoc";
    static String keyName="P_or_D";
    SharedPreferences SP;
    Button PatientButton,DoctorButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_selection_2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth = FirebaseAuth.getInstance();
        PatientButton=findViewById(R.id.btn_patient);
        DoctorButton=findViewById(R.id.btn_Doctor);
        SP=getSharedPreferences(SPName,MODE_PRIVATE);
    }

    public void startPatientLoginActivity(View view)
    {
        SharedPreferences.Editor editor=SP.edit();
        editor.putString(keyName,PatientButton.getText().toString());
        editor.apply();
            startActivity(new Intent(getApplicationContext(), SignupActivity_3.class));
             finish();
    }

    public void startDoctorLoginActivity(View view)
    {
        SharedPreferences.Editor editor=SP.edit();
        editor.putString(keyName,DoctorButton.getText().toString());
        editor.apply();
        startActivity(new Intent(getApplicationContext(), SignupActivity_3.class));
        finish();

    }
}