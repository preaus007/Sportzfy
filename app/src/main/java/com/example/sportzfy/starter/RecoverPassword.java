package com.example.sportzfy.starter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.example.sportzfy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class RecoverPassword extends AppCompatActivity {

    EditText emailEditText;
    Button sendBtn;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starter_activity_recover_password);

        emailEditText = findViewById(R.id.email_editText);
        sendBtn = findViewById(R.id.recover_password_btn);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        sendBtn.setOnClickListener(v -> recoverPassword());
    }

    private void recoverPassword() {
        String email = emailEditText.getText().toString().trim();

        if(email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toasty.error(getApplicationContext(), "Invalid email!", Toasty.LENGTH_SHORT, true).show();
        }
        else {
            Query query = database.getReference("users").orderByChild("email").equalTo(email);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    Toasty.info(getApplicationContext(), "Check your email", Toasty.LENGTH_SHORT, true).show();
                                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                                    finish();
                                });
                    }
                    else{
                        Toasty.warning(getApplicationContext(), "Email don't found!", Toasty.LENGTH_SHORT, true).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }
}