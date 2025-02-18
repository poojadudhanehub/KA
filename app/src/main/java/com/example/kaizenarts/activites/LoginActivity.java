package com.example.kaizenarts.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import com.example.kaizenarts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure this refers to your login layout

        // Hide the action bar for a cleaner look
       // if (getSupportActionBar() != null) {
          //  getSupportActionBar().hide();
       // }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Close login activity
        }

        // Initialize UI elements
        email = findViewById(R.id.et_email); // Replace with actual ID from your login layout XML
        password = findViewById(R.id.et_password); // Replace with actual ID from your login layout XML
    }

    // Login method
    public void signIn(View view) {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters long");
            password.requestFocus();
            return;
        }

        // Disable the button to prevent multiple clicks
        view.setEnabled(false);

        // Firebase authentication for user login
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Enable the button after processing
                        view.setEnabled(true);

                        if (task.isSuccessful()) {

                            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true); // User is now logged in
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Check if the user came from DetailedActivity
                            Intent intent = getIntent();
                            String previousActivity = intent.getStringExtra("from");

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            finish(); // Close current activity
                        } else {
                            String errorMessage = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Login failed. Please try again.";
                            Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void signUp(View view) {
        // Remove any check for onboarding, just navigate to the registration screen
        startActivity(new Intent(LoginActivity.this, registrationActivity.class));
        finish(); // Close current activity
    }


    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
