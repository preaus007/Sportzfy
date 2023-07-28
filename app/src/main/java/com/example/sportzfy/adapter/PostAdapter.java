package com.example.sportzfy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportzfy.R;
import com.example.sportzfy.helperActivity.CommentActivity;
import com.example.sportzfy.models.PostModel;
import com.example.sportzfy.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<PostModel> posts;
    Context context;

    public PostAdapter(ArrayList<PostModel> posts, Context context) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_post, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel model = posts.get(position);
        final Boolean[] liked = {false};

        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.postImage);

        String description = model.getPostDescription();
        if (description.equals("")) {
            holder.postDescription.setVisibility(View.GONE);
        } else {
            holder.postDescription.setVisibility(View.VISIBLE);
            holder.postDescription.setText(description);
        }

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfileImage())
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(holder.profileImg);

                        holder.userName.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        long timestamp = model.getPostedAt(); // Replace this with your timestamp value in milliseconds
        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Replace the format string as per your needs
        String formattedDate = sdf.format(date);
        holder.post_time.setText(formattedDate);
        holder.likes.setText(model.getPostLikes() + "");
        holder.comments.setText(model.getPostComments()+ "");

        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            liked[0] = true;
                            holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_fill, 0, 0, 0);
                        }
                        else{
                            liked[0] = false;
                            holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!liked[0]) {
                    holder.likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(model.getPostId())
                                    .child("likes")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("posts")
                                                    .child(model.getPostId())
                                                    .child("postLikes")
                                                    .setValue(model.getPostLikes() + 1)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            liked[0] = true;
                                                            holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_fill, 0, 0, 0);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                }
                else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("posts")
                            .child(model.getPostId())
                            .child("likes")
                            .child(FirebaseAuth.getInstance().getUid())
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("postLikes")
                                            .setValue(model.getPostLikes() - 1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    liked[0] = false;
                                                    holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                                                }
                                            });
                                }
                            });
                }
            }
        });

        holder.comments.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("postedBy", model.getPostedBy());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        CircleImageView profileImg;
        TextView postDescription, userName, post_time;
        TextView likes, comments;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            profileImg = itemView.findViewById(R.id.profileImg);
            postDescription = itemView.findViewById(R.id.post_description);
            userName = itemView.findViewById(R.id.userName);
            post_time = itemView.findViewById(R.id.post_time);
            likes = itemView.findViewById(R.id.like);
            comments = itemView.findViewById(R.id.comment);
        }
    }

}
