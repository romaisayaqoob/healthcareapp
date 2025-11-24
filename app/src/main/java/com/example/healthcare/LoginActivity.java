package com.example.healthcare;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPass;
    Button btnLogin, btnGoSignup;
    DBHelper dbHelper; // instead of userDB



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // At the very top of onCreate in LoginActivity, add:
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed", e);
            Toast.makeText(this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoSignup = findViewById(R.id.btnGotoSignup);

        dbHelper = new DBHelper();

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String pass = edtPass.getText().toString();

            dbHelper.checkUserRole(email, pass, role -> {
                if (role != null) {
                    SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                    sp.edit()
                            .putBoolean("logged_in", true)
                            .putString("user_email", email)
                            .putString("user_role", role)
                            .apply();

                    if (role.equals("Doctor")) {
                        startActivity(new Intent(LoginActivity.this, DoctorMainActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });



        btnGoSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class))
        );
    }
}
