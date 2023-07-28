package com.example.sportzfy.starter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.sportzfy.R;
import com.example.sportzfy.sessions.SessionManager;
import com.example.sportzfy.tabs.Dashboard;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout emailLayout, passwordLayout;
    TextInputEditText emailEditText, passwordEditText;
    AppCompatButton signIn;
    Button forgetPassword;
    TextView signUp;
    CheckBox rememberMe;

    SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starter_activity_sign_in);

        emailLayout = findViewById(R.id.emailInputLayout);
        passwordLayout = findViewById(R.id.passwordInputLayout);
        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        signIn = findViewById(R.id.sign_in);
        forgetPassword = findViewById(R.id.forget_password);
        signUp = findViewById(R.id.sign_up);
        rememberMe = findViewById(R.id.remember_me);

        sessionManager = new SessionManager(SignIn.this, SessionManager.REMEMBER_ME_SESSION);
        if(sessionManager.checkRememberMe()){
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailsFromSession();
            emailEditText.setText(rememberMeDetails.get(SessionManager.KEY_REMEMBER_ME_EMAIL));
            passwordEditText.setText(rememberMeDetails.get(SessionManager.KEY_REMEMBER_ME_PASSWORD));
        }

        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(this);
        signIn.setOnClickListener(v -> userLogin());
        forgetPassword.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RecoverPassword.class)));
    }

    private void userLogin() {

        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

        if (rememberMe.isChecked()) {
            sessionManager.createRememberMeSession(email, password);
        }
        else {
            sessionManager.logoutUserFromSession();
        }

        if (validateEmail(email) && validatePassword(password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toasty.success(getApplicationContext(), "User login successfully", Toast.LENGTH_SHORT, true).show();
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            finish();
                        } else {
                            Toasty.error(getApplicationContext(), "SignIn Failed" + task.getException(), Toast.LENGTH_SHORT, true).show();
                        }
                    });
        }
    }

    private boolean validateEmail(String email) {
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(null);
            return true;
        } else {
            emailLayout.setError("Invalid email");
            return false;
        }
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            passwordLayout.setError("Insert password");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_up) {
            startActivity(new Intent(getApplicationContext(), SignUp.class));
        }
    }
}