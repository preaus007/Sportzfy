package com.example.sportzfy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportzfy.R;
import com.example.sportzfy.models.CommentModel;
import com.example.sportzfy.models.UserModel;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{

    Context context;
    ArrayList<CommentModel>comments;

    public CommentAdapter(Context context, ArrayList<CommentModel> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentModel commentModel = comments.get(position);
        holder.comment.setText(commentModel.getComment());

        long timestamp = commentModel.getCommentedAt(); // Replace this with your timestamp value in milliseconds
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault()); // Replace the format string as per your needs
        String formattedDate = sdf.format(date);
        holder.time.setText(formattedDate);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(commentModel.getCommentedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImg;
        TextView userName, time, comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profileImg);
            userName = itemView.findViewById(R.id.userName);
            time = itemView.findViewById(R.id.post_time);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
