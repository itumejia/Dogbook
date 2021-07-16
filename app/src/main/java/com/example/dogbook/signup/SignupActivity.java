package com.example.dogbook.signup;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dogbook.login.LoginActivity;
import com.example.dogbook.main.MainActivity;
import com.example.dogbook.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Date;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText etUsername;
    EditText etPassword;
    EditText etConfirmPassword;
    EditText etBreed;
    DatePicker dpBirthday;
    CheckBox cbLookingForPlaymates;
    EditText etOwnerName;
    EditText etOwnerContact;
    Button btnSignup;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginActivity();
            }
        });

    }

    private void initView() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etBreed = findViewById(R.id.etBreed);
        dpBirthday = findViewById(R.id.dpBirthday);
        cbLookingForPlaymates = findViewById(R.id.cbLookingForPlaymates);
        etOwnerName = findViewById(R.id.etOwnerName);
        etOwnerContact = findViewById(R.id.etOwnerContact);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void signUp() {
        //Password confirmation was invalid
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwor confirmation was invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseUser user = new ParseUser();
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.put("ownerName", etOwnerName.getText().toString());
        user.put("breed", etBreed.getText().toString());
        user.put("birthday", getBirthdayDate());
        user.put("lookingForPlaymates", cbLookingForPlaymates.isChecked());
        if (!etOwnerContact.getText().toString().isEmpty()) {
            user.put("ownerContact", etOwnerContact.getText().toString());
        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                //Sign up succeeded
                if (e == null){
                    goMainActivity();
                }
                //Sign up did NOT succeed
                else {
                    //TODO: Show specific issues to user with different toasts
                    Toast.makeText(SignupActivity.this, "Signup was not possible", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Issue with signup", e);
                }
            }
        });
    }

    private Date getBirthdayDate() {
        Date date = new Date(dpBirthday.getYear() - 1900, dpBirthday.getMonth(), dpBirthday.getDayOfMonth());
        return date;
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}