package com.example.healthcare;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPass;
    Button btnLogin, btnGoSignup;

    FirebaseAuth auth;
    FirebaseDatabase realtimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);

        /*edtPass.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // right drawable
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (edtPass.getRight() - edtPass.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    // Toggle password visibility here
                    if(edtPass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                        edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    edtPass.setSelection(edtPass.getText().length());
                    return true;
                }
            }
            return false;
        });*/

        btnLogin = findViewById(R.id.btnLogin);
        btnGoSignup = findViewById(R.id.btnGotoSignup);

        auth = FirebaseAuth.getInstance();
        realtimeDB = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        );


        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString();
            String pass = edtPass.getText().toString();

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(result -> {

                        String uid = result.getUser().getUid();

                        // now get user role from realtime DB
                        realtimeDB.getReference("users")
                                .child(uid)
                                .get()
                                .addOnSuccessListener(snapshot -> {

                                    if (!snapshot.exists()) {
                                        Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    String role = snapshot.child("role").getValue(String.class);
                                    String name = snapshot.child("name").getValue(String.class);
                                    // Save session
                                    SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                                    sp.edit()
                                            .putBoolean("logged_in", true)
                                            .putString("user_uid", uid)
                                            .putString("user_email", email)
                                            .putString("user_role", role)
                                            .putString("user_name", name)
                                            .apply();

                                    if ("Doctor".equals(role)) {
                                        startActivity(new Intent(LoginActivity.this, DoctorMainActivity.class));
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                    finish();

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                    );

        });

        btnGoSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class))
        );
    }
}
