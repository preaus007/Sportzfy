package com.example.sportzfy.tabs;

import static com.example.sportzfy.sessions.SessionManager.USER_LOGIN_SESSION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportzfy.R;
import com.example.sportzfy.helperClasses.ApiServices;
import com.example.sportzfy.helperClasses.CustomLoading;
import com.example.sportzfy.helperClasses.District;
import com.example.sportzfy.helperClasses.DistrictResponse;
import com.example.sportzfy.models.PostModel;
import com.example.sportzfy.models.UserModel;
import com.example.sportzfy.sessions.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private CircleImageView profileImg;
    private AppCompatButton editProfile, saveProfile;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private TextView titleName, titleUsername;
    private TextView profileName, profilePhone, profileEmail, profileAddress, profileBirthday;
    private TextView totalPosts, totalLikes;

    private static final int GALLERY_IMAGE_CODE = 200;
    Uri image_uri = null;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    private ImageView editName, editPhone, editAddress, editDOB;

    CustomLoading dialog;

    String _image, _password;

    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    String selectedDistrict, selectedState;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_activity_user_profile);

        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        profileImg = findViewById(R.id.profileImg);
        profileName = findViewById(R.id.userFullName);
        profileEmail = findViewById(R.id.userEmail);
        profilePhone = findViewById(R.id.userPhone);
        profileBirthday = findViewById(R.id.userDOB);
        profileAddress = findViewById(R.id.userAddress);
        totalPosts = findViewById(R.id.totalPostCount);
        totalLikes = findViewById(R.id.totalLikeCount);

        editProfile = findViewById(R.id.editBtn);
        saveProfile = findViewById(R.id.saveBtn);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editDOB = findViewById(R.id.editDOB);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        dialog = new CustomLoading(UserProfile.this);

        sessionManager = new SessionManager(UserProfile.this, USER_LOGIN_SESSION);
        userDetails = sessionManager.getUsersDetailsFromSession();

        profileImg.setOnClickListener(v -> changePhoto());

        editProfile.setOnClickListener(v -> {
            editName.setVisibility(View.VISIBLE);
            editPhone.setVisibility(View.VISIBLE);
            editAddress.setVisibility(View.VISIBLE);
            editDOB.setVisibility(View.VISIBLE);
            saveProfile.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.GONE);
        });

        saveProfile.setOnClickListener(v -> saveInformation());

        editName.setOnClickListener(v -> {
            View view = LayoutInflater.from(UserProfile.this).inflate(R.layout.change_name_dialog, null);
            TextInputEditText newNameEditText = view.findViewById(R.id.newNameEditText);
            TextView oldNameText = view.findViewById(R.id.oldNameText);

            oldNameText.setText(profileName.getText().toString());

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(UserProfile.this)
                    .setTitle("Change Name")
                    .setView(view)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        profileName.setText(newNameEditText.getText().toString());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Close", (dialog, which) -> dialog.dismiss()).create();

            alertDialog.show();
        });

        editPhone.setOnClickListener(v -> {
            View view = LayoutInflater.from(UserProfile.this).inflate(R.layout.change_mobile_no_dialog, null);

            CountryCodePicker countryCode = view.findViewById(R.id.country_code);
            TextInputEditText phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(UserProfile.this)
                    .setTitle("Change Phone ")
                    .setView(view)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        String phoneNumber = phoneNumberEditText.getText().toString().trim();
                        String _phone = countryCode.getSelectedCountryCodeWithPlus() + phoneNumber;
                        profilePhone.setText(_phone);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Close", (dialog, which) -> dialog.dismiss()).create();

            alertDialog.show();
        });

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(UserProfile.this).inflate(R.layout.change_address_layout, null);

                Spinner spinnerDistrict = view.findViewById(R.id.spinnerDistrict);
                Spinner spinnerSubdistrict = view.findViewById(R.id.spinnerSubdistrict);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://raw.githubusercontent.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiServices apiService = retrofit.create(ApiServices.class);

                Call<DistrictResponse> call = apiService.getChittagongDistrictData();
                call.enqueue(new Callback<DistrictResponse>() {
                    @Override
                    public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                        if (response.isSuccessful()) {
                            DistrictResponse districtResponse = response.body();
                            if (districtResponse != null) {
                                List<District> districts = districtResponse.getDistricts();

                                // Populate districts in the first spinner
                                List<String> districtNames = new ArrayList<>();
                                for (District district : districts) {
                                    districtNames.add(district.getDistrict());
                                }
                                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(UserProfile.this, android.R.layout.simple_spinner_dropdown_item, districtNames);
                                spinnerDistrict.setAdapter(districtAdapter);

                                // Set up a listener to handle the selection of the first spinner
                                spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        District selectedDistrict = districts.get(position);
                                        List<String> subdistricts = selectedDistrict.getSubdistrict();

                                        // Populate subdistricts in the second spinner
                                        ArrayAdapter<String> subdistrictAdapter = new ArrayAdapter<>(UserProfile.this, android.R.layout.simple_spinner_dropdown_item, subdistricts);
                                        spinnerSubdistrict.setAdapter(subdistrictAdapter);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Handle case when nothing is selected
                                    }
                                });
                            }
                        } else {
                            // Handle error
                            Log.e("ApiCall", "Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<DistrictResponse> call, Throwable t) {
                        // Handle failure
                        Log.e("ApiCall", "Failure: " + t.getMessage());
                    }
                });

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(UserProfile.this)
                        .setTitle("Change Address ")
                        .setView(view)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            String district = spinnerDistrict.getSelectedItem().toString().trim();
                            String subDistrict = spinnerSubdistrict.getSelectedItem().toString().trim();
                            String address = district + ", " + subDistrict;
                            profileAddress.setText(address);
                            dialog.dismiss();
                        })
                        .setNegativeButton("Close", (dialog, which) -> dialog.dismiss()).create();

                alertDialog.show();
            }
        });

        editDOB.setOnClickListener(v -> {
            DialogFragment datePicker = new com.example.sportzfy.models.DatePicker();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });
    }

    private void changePhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_IMAGE_CODE) {
                if (data != null) {
                    image_uri = data.getData();
                    profileImg.setImageURI(image_uri);
                    saveProfilePhoto();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveProfilePhoto() {
        final StorageReference storageReference = storage.getReference().child("profile_photo").child(mAuth.getUid());
        dialog.startLoadingDialog("Saving profile...");

        storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Toasty.success(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT, true).show();
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    database.getReference().child("users").child(mAuth.getUid()).child("profileImage").setValue(uri.toString());
                    dialog.dismissDialog();
                }
            });
        });
    }

    private void saveInformation(){

        String _name = profileName.getText().toString();
        String _username = titleUsername.getText().toString();
        String _email = profileEmail.getText().toString();
        String _phone = profilePhone.getText().toString();
        String _birthday = profileBirthday.getText().toString();
        String _address = profileAddress.getText().toString();

        UserModel user = new UserModel();

        user.setProfileImage(_image);
        user.setName(_name);
        user.setUsername(_username);
        user.setEmail(_email);
        user.setPhone(_phone);
        user.setBirthday(_birthday);
        user.setAddress(_address);
        user.setPassword(_password);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserProfile.this, "User information updated!", Toast.LENGTH_SHORT).show();

                        SessionManager sessionManager = new SessionManager(UserProfile.this, USER_LOGIN_SESSION);
                        sessionManager.createLoginSession(_name, _username, _email, _address, _phone, _birthday);

                    }
                });

        editProfile.setVisibility(View.VISIBLE);
        saveProfile.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);
        editPhone.setVisibility(View.GONE);
        editAddress.setVisibility(View.GONE);
        editDOB.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = database.getReference();

        Query query = reference.child("posts").orderByChild("postedBy").equalTo(mAuth.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalPosts.setText(snapshot.getChildrenCount()+"");
                long likes = 0;
                for (DataSnapshot snaps : snapshot.getChildren()) {
                    // Access the user data using snapshot.getValue()
                    PostModel postModel = snaps.getValue(PostModel.class);
                    likes += postModel.getPostLikes();
                }
                totalLikes.setText(likes+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        reference.child("users").child(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel user = snapshot.getValue(UserModel.class);

                            _image = user.getProfileImage();
                            _password = user.getPassword();

                            Picasso.get()
                                    .load(_image)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(profileImg);

                            profileName.setText(user.getName());
                            titleName.setText(user.getName());
                            titleUsername.setText(user.getUsername());
                            profileEmail.setText(user.getEmail());
                            profilePhone.setText(user.getPhone());
                            profileAddress.setText(user.getAddress());
                            profileBirthday.setText(user.getBirthday());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String dob = DateFormat.getDateInstance().format(c.getTime());
        profileBirthday.setText(dob);
    }
}