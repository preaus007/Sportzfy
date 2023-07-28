package com.example.sportzfy.helperActivity;

import static com.example.sportzfy.sessions.SessionManager.USER_LOGIN_SESSION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportzfy.R;
import com.example.sportzfy.models.PostModel;
import com.example.sportzfy.models.UserModel;
import com.example.sportzfy.services.BlogActivity;
import com.example.sportzfy.sessions.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CreatePostActivity extends AppCompatActivity {

    Button post;
    CircleImageView userImage;
    TextView userName, selectPhoto;
    ImageView postImage;
    EditText postDescription;

    private static final int GALLERY_IMAGE_CODE = 200;

    Uri image_uri = null;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("Create Post");
        }

        sessionManager = new SessionManager(CreatePostActivity.this, USER_LOGIN_SESSION);
        userDetails = sessionManager.getUsersDetailsFromSession();

        post = findViewById(R.id.post_btn);
        userImage = findViewById(R.id.profileImg);
        userName = findViewById(R.id.userName);
        selectPhoto = findViewById(R.id.uploadImg);
        postImage = findViewById(R.id.postImg);
        postDescription = findViewById(R.id.post_description);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String desc = postDescription.getText().toString();

                if (!desc.isEmpty()) {
                    post.setBackgroundColor(R.color.colorSecondary);
                    post.setEnabled(true);
                } else {
                    post.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        selectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_IMAGE_CODE);
        });

        dialog = new Dialog(CreatePostActivity.this);

        post.setOnClickListener(v -> uploadPost());

    }

    private void uploadPost() {

        dialog.startLoadingDialog();

        final String timeStamp = String.valueOf(System.currentTimeMillis());
        final String description = postDescription.getText().toString();
        final StorageReference reference = storage.getReference().child("post_photo").child(mAuth.getUid()).child(timeStamp);

        reference.putFile(image_uri)
                .addOnSuccessListener(taskSnapshot -> {
//                Toasty.success(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT, true).show();
                    reference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    PostModel post = new PostModel();
                                    post.setPostImage(uri.toString());
                                    post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                    post.setPostDescription(description);
                                    post.setPostedAt(new Date().getTime());

                                    database.getReference().child("posts")
                                            .push()
                                            .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismissDialog();
                                                    Toasty.success(getApplicationContext(), "Post uploaded!", Toast.LENGTH_SHORT, true).show();
                                                    startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                                                    finish();
                                                }
                                            });

                                    postDescription.setText("");
                                    postImage.setImageURI(null);
                                }
                            });
                });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_IMAGE_CODE) {
                if (data != null) {
                    image_uri = data.getData();
                    postImage.setImageURI(image_uri);
                    postImage.setVisibility(View.VISIBLE);
                    post.setBackgroundColor(R.color.colorSecondary);
                    post.setEnabled(true);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        database.getReference().child("users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            Picasso.get()
                                    .load(user.getProfileImage())
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(userImage);

                            userName.setText(user.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}