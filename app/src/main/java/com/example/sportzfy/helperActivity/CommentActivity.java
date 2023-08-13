package com.example.sportzfy.helperActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportzfy.R;
import com.example.sportzfy.adapter.CommentAdapter;
import com.example.sportzfy.models.CommentModel;
import com.example.sportzfy.models.PostModel;
import com.example.sportzfy.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    ImageView postImage, postComment;
    CircleImageView userImage;
    TextView userName, postDescription;
    EditText comments;
    RecyclerView commentRV;

    Intent intent;

    String postId, postedBy;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    PostModel post;

    ArrayList<CommentModel> lists = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("Comments");
        }

        postImage = findViewById(R.id.post_image);
        postComment = findViewById(R.id.post_comment);
        userImage = findViewById(R.id.profileImg);
        userName = findViewById(R.id.userName);
        postDescription = findViewById(R.id.post_description);
        comments = findViewById(R.id.edit_comment);
        commentRV = findViewById(R.id.comment_RV);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

//        Toast.makeText(this, postId, Toast.LENGTH_SHORT).show();

        getPostImageAndDescription();

        getUserImageAndName();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleComments();
            }
        });

        CommentAdapter commentAdapter = new CommentAdapter(this, lists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        commentRV.setLayoutManager(layoutManager);
        commentRV.setAdapter(commentAdapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lists.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            CommentModel commentModel = dataSnapshot.getValue(CommentModel.class);
                            lists.add(commentModel);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void handleComments() {
        if(!comments.getText().toString().isEmpty()){
            CommentModel comment = new CommentModel();
            comment.setComment(comments.getText().toString());
            comment.setCommentedAt(new Date().getTime());
            comment.setCommentedBy(FirebaseAuth.getInstance().getUid());

            database.getReference()
                    .child("posts")
                    .child(postId)
                    .child("comments")
                    .push()
                    .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference()
                                    .child("posts")
                                    .child(postId)
                                    .child("postComments")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int commentCount = 0;
                                            if (snapshot.exists()) {
                                                commentCount = snapshot.getValue(Integer.class);
                                            }
                                            database.getReference()
                                                    .child("posts")
                                                    .child(postId)
                                                    .child("postComments")
                                                    .setValue(commentCount + 1)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            comments.setText("");
                                                            Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    });
        } else {
            Toast.makeText(this, "no comments", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserImageAndName() {
        database.getReference()
                .child("users")
                .child(postedBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
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

    private void getPostImageAndDescription() {
        database.getReference()
                .child("posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            post = snapshot.getValue(PostModel.class);
                            Picasso.get()
                                    .load(post.getPostImage())
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(postImage);
                            postDescription.setText(post.getPostDescription());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}