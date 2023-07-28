package com.example.sportzfy.starter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportzfy.R;
import com.example.sportzfy.models.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    TextInputEditText fullNameEditText, userNameEditText, emailEditText, passwordEditText;
    TextInputLayout passwordTextLayout, userNameTextLayout;
    AppCompatButton signUp;
    TextView signIn;

    boolean proceed = true;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starter_activity_sign_up);

        fullNameEditText = findViewById(R.id.full_name_editText);
        userNameEditText = findViewById(R.id.user_name_editText);
        emailEditText = findViewById(R.id.email_editText);
        passwordTextLayout = findViewById(R.id.password_textLayout);
        passwordEditText = findViewById(R.id.password_editText);
        userNameTextLayout = findViewById(R.id.userNameTextLayout);
        signUp = findViewById(R.id.sign_up);
        signIn = findViewById(R.id.sign_in);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        signIn.setOnClickListener(this);

        userNameEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus) {
                checkUser();
                userNameTextLayout.setEndIconVisible(false);
            }
        });

        signUp.setOnClickListener(view -> userRegistration());
    }

    private void userRegistration() {
        String regName = Objects.requireNonNull(fullNameEditText.getText()).toString();
        String regUsername = Objects.requireNonNull(userNameEditText.getText()).toString();
        String regEmail = Objects.requireNonNull(emailEditText.getText()).toString();
        String regPassword = Objects.requireNonNull(passwordEditText.getText()).toString();

        boolean validUser = validateName(regName) && validateUsername(regUsername) && validateEmail(regEmail) && validatePassword(regPassword) && proceed;

        if(validUser){
            mAuth.createUserWithEmailAndPassword(regEmail, regPassword)
                    .addOnCompleteListener(this, task -> {

                        if (task.isSuccessful()) {
                            UserModel user = new UserModel(regName, regUsername, regEmail, regPassword);
                            String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            reference.child(id).setValue(user);
                            Toasty.success(getApplicationContext(), "Registration Successful.", Toast.LENGTH_SHORT, true).show();
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
                            finish();
                        } else {
                            Toasty.error(getApplicationContext(), "SignUp Failed" + task.getException(), Toast.LENGTH_SHORT, true).show();
                        }
                    });
        }
    }

    private boolean validateName(String name){
        if (name.isEmpty()) {
            fullNameEditText.setError("Name cannot be empty");
            return false;
        }
        else {
            fullNameEditText.setError(null);
            return true;
        }
    }

    private boolean validateUsername(String username){
        if (username.isEmpty()) {
            userNameEditText.setError("Username cannot be empty");
            return false;
        }
        else {
            userNameEditText.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Insert valid email");
            return false;
        }
        else {
            emailEditText.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password){
        if (password.isEmpty()) {
            passwordTextLayout.setError("Password cannot be empty");
            return false;
        }
        else {
            passwordTextLayout.setError(null);
            return true;
        }

    }

    private void checkUser() {

        String shortName = Objects.requireNonNull(userNameEditText.getText()).toString();

        if (!shortName.isEmpty()) {
            userNameEditText.setError(null);

            Query query = reference.orderByChild("username").equalTo(shortName);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userNameEditText.setError("Username already exist");
                        proceed = false;
                    } else {
                        userNameEditText.setError(null);
                        proceed = true;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
        else {
            userNameEditText.setError("Username cannot be empty");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in) {
            startActivity(new Intent(getApplicationContext(), SignIn.class));
        }
    }
}