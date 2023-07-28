package com.example.sportzfy.fragments;

import static android.content.ContentValues.TAG;
import static com.example.sportzfy.sessions.SessionManager.USER_LOGIN_SESSION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sportzfy.R;
import com.example.sportzfy.models.UserModel;
import com.example.sportzfy.services.BlogActivity;
import com.example.sportzfy.services.BmiActivity;
import com.example.sportzfy.services.BrowserActivity;
import com.example.sportzfy.services.ClassifyActivity;
import com.example.sportzfy.sessions.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class HomeFragment extends Fragment {

    TextView userName, greetings;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userName = view.findViewById(R.id.username);
        greetings = view.findViewById(R.id.greeting_wish);

        sessionManager = new SessionManager(getContext(), USER_LOGIN_SESSION);
        userDetails = sessionManager.getUsersDetailsFromSession();

        CardView blog = view.findViewById(R.id.blog);
        CardView browser = view.findViewById(R.id.browser);
        CardView classify = view.findViewById(R.id.classify);
        CardView bmiCalculator = view.findViewById(R.id.bmi);

        blog.setOnClickListener(v -> startActivity(new Intent(getContext(), BlogActivity.class)));
        browser.setOnClickListener(v -> startActivity(new Intent(getContext(), BrowserActivity.class)));
        classify.setOnClickListener(v -> startActivity(new Intent(getContext(), ClassifyActivity.class)));
        bmiCalculator.setOnClickListener(v -> startActivity(new Intent(getContext(), BmiActivity.class)));

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").child(Objects.requireNonNull(mAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            UserModel user = snapshot.getValue(UserModel.class);
                            userName.setText(" " + user.getUsername());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 4 && timeOfDay < 11) {
            greetings.setText("Good Morning");
        } else if (timeOfDay >= 11 && timeOfDay < 15) {
            greetings.setText("Good Noon");
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            greetings.setText("Good Afternoon");
        } else if (timeOfDay >= 18 && timeOfDay < 22) {
            greetings.setText("Good Evening");
        } else if (timeOfDay >= 22) {
            greetings.setText("Good Night");
        }
    }
}