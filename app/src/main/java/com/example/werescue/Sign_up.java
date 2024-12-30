package com.example.werescue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log; // Import the Log class
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.werescue.R.id;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Sign_up extends Activity {

    private static final String TAG = "Sign_up"; // Tag for logging
    private boolean passwordShowing = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        Log.d(TAG, "onCreate: Sign_up activity started");

        mAuth = FirebaseAuth.getInstance();
        final EditText emailET = (EditText) findViewById(R.id.emailET);
        final EditText fullNameET = (EditText) findViewById(R.id.fullNameET);
        final EditText passwordET = (EditText) findViewById(R.id.passwordET);
        final Button signUpButton = (Button) findViewById(R.id.btn_register);
        final ImageView passwordIcon = findViewById(R.id.pass_icon);
        final EditText confirmPasswordET = (EditText) findViewById(R.id.conf_passwordET);
        final TextView loginButton = findViewById(R.id.btn_login);
        final TextView passwordErrorTV = findViewById(R.id.passwordErrorTV);

        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Password visibility icon clicked");
                if (passwordShowing) {
                    passwordShowing = false;
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirmPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.show_pass);
                    Log.d(TAG, "Password visibility set to hidden");
                } else {
                    passwordShowing = true;
                    passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.not_show_pass);
                    Log.d(TAG, "Password visibility set to visible");
                }
                // Move the cursor to the end of the text
                passwordET.setSelection(passwordET.length());
                confirmPasswordET.setSelection(confirmPasswordET.length());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                Intent intent = new Intent(Sign_up.this, Login.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign up button clicked");

                String email = emailET.getText().toString();
                final String fullName = fullNameET.getText().toString();
                String password = passwordET.getText().toString();
                String confirmPassword = confirmPasswordET.getText().toString();

                Log.d(TAG, "Email: " + email);
                Log.d(TAG, "Full Name: " + fullName);

                if (email.isEmpty() && fullName.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()) {
                    Log.w(TAG, "All fields are empty");
                    Toast.makeText(Sign_up.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.isEmpty()) {
                    Log.w(TAG, "Email field is empty");
                    Toast.makeText(Sign_up.this, "Email field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.matches("^[\\w.-]+@(gmail|outlook|yahoo|icloud)\\.com$")) {
                    Log.w(TAG, "Invalid email format: " + email);
                    Toast.makeText(Sign_up.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fullName.isEmpty()) {
                    Log.w(TAG, "Full Name field is empty");
                    Toast.makeText(Sign_up.this, "Full Name field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Log.w(TAG, "Password field is empty");
                    Toast.makeText(Sign_up.this, "Password field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Log.w(TAG, "Password is too short");
                    Toast.makeText(Sign_up.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                    Log.w(TAG, "Password does not meet complexity requirements");
                    passwordErrorTV.setText("Password must contain at least one uppercase letter, one number and one symbol");
                    passwordErrorTV.setVisibility(View.VISIBLE);
                    return;
                } else {
                    passwordErrorTV.setVisibility(View.GONE);
                }
                if (confirmPassword.isEmpty()) {
                    Log.w(TAG, "Confirm Password field is empty");
                    Toast.makeText(Sign_up.this, "Confirm Password field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Log.w(TAG, "Passwords do not match");
                    Toast.makeText(Sign_up.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "Attempting to create user with email: " + email);
                // Sign up the user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Sign_up.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User created successfully");
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName)
                                            .build();

                                    if (user != null) {
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User profile updated successfully");
                                                            Toast.makeText(Sign_up.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(Sign_up.this, Login.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Log.e(TAG, "Profile update failed");
                                                            Toast.makeText(Sign_up.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.e(TAG, "Sign up failed", task.getException());
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Sign_up.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}