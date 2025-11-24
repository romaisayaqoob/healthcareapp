package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText edtName, edtEmail, edtPass;
    Button btnSignup;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Log.d(TAG, "onCreate: SignupActivity started");

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed", e);
            Toast.makeText(this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);

        dbHelper = new DBHelper();
        Log.d(TAG, "DBHelper initialized");

        btnSignup.setOnClickListener(v -> {
            Log.d(TAG, "Signup button clicked");

            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();

            Spinner spinnerRole = findViewById(R.id.spinnerRole);
            String role = spinnerRole.getSelectedItem().toString();

            Log.d(TAG, "Name: " + name);
            Log.d(TAG, "Email: " + email);
            Log.d(TAG, "Password length: " + pass.length());
            Log.d(TAG, "Role: " + role);

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Empty fields detected");
                return;
            }

            Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show();

            dbHelper.registerUser(name, email, pass, role,
                    unused -> {
                        Log.d(TAG, "Account created successfully");
                        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    },
                    e -> {
                        Log.e(TAG, "Error creating account", e);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );
        });
    }
}