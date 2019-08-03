package com.daniel.ethan.paymentmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button loginBtn;
    private TextView signUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {

        }
        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_pwd);
        loginBtn = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.text_sign_up);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                if (email.isEmpty()) {
                    emailField.setError("Enter your e-mail");
                    emailField.requestFocus();
                } else if (password.isEmpty()) {
                    passwordField.setError("Enter your password");
                    passwordField.requestFocus();
                }

            }
        });
    }
}
