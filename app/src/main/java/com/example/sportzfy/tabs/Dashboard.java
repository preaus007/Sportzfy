package com.example.sportzfy.tabs;

import static android.content.ContentValues.TAG;
import static com.example.sportzfy.sessions.SessionManager.USER_LOGIN_SESSION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.sportzfy.R;
import com.example.sportzfy.fragments.AboutFragment;
import com.example.sportzfy.fragments.FindMeFragment;
import com.example.sportzfy.fragments.HomeFragment;
import com.example.sportzfy.fragments.RateUsFragment;
import com.example.sportzfy.fragments.SettingsFragment;
import com.example.sportzfy.models.UserModel;
import com.example.sportzfy.sessions.SessionManager;
import com.example.sportzfy.starter.SignIn;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CircleImageView userImage;

    DrawerLayout drawerLayout;
    Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_activity_dashboard);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        userImage = findViewById(R.id.user_image);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        sessionManager = new SessionManager(Dashboard.this, USER_LOGIN_SESSION);
        userDetails = sessionManager.getUsersDetailsFromSession();

        userImage.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UserProfile.class)));

    }

    private void signOut() {
        AlertDialog.Builder signOutAlert = new AlertDialog.Builder(Dashboard.this);
        signOutAlert.setTitle("Log out")
                .setMessage("Are you sure to log out?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    mAuth.signOut();
                    sessionManager.logoutUserFromSession();
                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {});

        AlertDialog dialog = signOutAlert.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        database.getReference().child("users").child(Objects.requireNonNull(mAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){

                            UserModel user = snapshot.getValue(UserModel.class);

                            Picasso.get()
                                    .load(Objects.requireNonNull(user).getProfileImage())
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(userImage);

                            String _name = user.getName();
                            String _username = user.getUsername();
                            String _email = user.getEmail();
                            String _password = user.getPassword();
                            String _address = user.getAddress();
                            String _phone = user.getPhone();
                            String _birthday = user.getBirthday();

                            sessionManager.createLoginSession(_name, _username, _email, _address, _phone, _birthday);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FindMeFragment()).commit();
                break;
            case R.id.nav_rating:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RateUsFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                break;
            case R.id.nav_logout:
                signOut();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}