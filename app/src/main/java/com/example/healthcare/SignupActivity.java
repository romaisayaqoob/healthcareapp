package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPass;
    Button btnSignup;

    FirebaseAuth auth;
    FirebaseDatabase realtimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        Spinner spinnerRole = findViewById(R.id.spinnerRole);

        auth = FirebaseAuth.getInstance();
        realtimeDB = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        );


        btnSignup.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show();

            auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(result -> {

                String uid = result.getUser().getUid();  // <-- THIS IS YOUR ID

                User user = new User(uid, name, email, role); // <-- FIXED

                realtimeDB.getReference("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );

            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Signup Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        });
    }
}
