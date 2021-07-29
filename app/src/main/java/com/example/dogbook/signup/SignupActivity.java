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
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.Date;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    public static final int YEAR_FIX = 1900;

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etBreed;
    private DatePicker dpBirthday;
    private CheckBox cbLookingForPlaymates;
    private EditText etOwnerName;
    private EditText etOwnerContact;
    private TransitionButton btnSignup;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignup.startAnimation();
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
        btnSignup = findViewById(R.id.btnSignup);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etBreed = findViewById(R.id.etBreed);
        dpBirthday = findViewById(R.id.dpBirthday);
        cbLookingForPlaymates = findViewById(R.id.cbLookingForPlaymates);
        etOwnerName = findViewById(R.id.etOwnerName);
        etOwnerContact = findViewById(R.id.etOwnerContact);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void signUp() {
        //Password confirmation was invalid
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Password confirmation was invalid", Toast.LENGTH_SHORT).show();
            btnSignup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
            return;
        }
        ParseUser user = new ParseUser();
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.put("ownerName", etOwnerName.getText().toString());
        user.put("breed", etBreed.getText().toString());
        user.put("birthday", getBirthdayDate());
        user.put("lookingForPlaymates", cbLookingForPlaymates.isChecked());
        String ownerContactInfo = etOwnerContact.getText().toString();
        if (!ownerContactInfo.isEmpty()) {
            user.put("ownerContact", ownerContactInfo);
        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                //Sign up succeeded
                if (e == null){
                    goMainActivity();
                    return;
                }
                //Sign up did NOT succeed
                //TODO: Show specific issues to user with different toasts
                btnSignup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                Toast.makeText(SignupActivity.this, "Signup was not possible", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Issue with signup", e);
            }
        });
    }

    private Date getBirthdayDate() {
        return new Date(dpBirthday.getYear() - YEAR_FIX, dpBirthday.getMonth(), dpBirthday.getDayOfMonth());
    }

    private void goMainActivity() {
        btnSignup.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
            @Override
            public void onAnimationStopEnd() {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}