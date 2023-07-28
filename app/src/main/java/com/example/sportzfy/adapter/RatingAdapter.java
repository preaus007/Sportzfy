package com.example.sportzfy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportzfy.R;
import com.example.sportzfy.models.RatingModel;
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

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.viewHolder>{

    Context context;
    ArrayList<RatingModel>ratings;

    public RatingAdapter(Context context, ArrayList<RatingModel> ratings) {
        this.context = context;
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_post, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        RatingModel ratingModel = ratings.get(position);

        String description = ratingModel.getReview();
        if (description.equals("")) {
            holder.review.setVisibility(View.GONE);
        } else {
            holder.review.setVisibility(View.VISIBLE);
            holder.review.setText(description);
        }

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(ratingModel.getRatedBy()).addValueEventListener(new ValueEventListener() {
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

        long timestamp = ratingModel.getRatedAt(); // Replace this with your timestamp value in milliseconds
        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Replace the format string as per your needs
        String formattedDate = sdf.format(date);
        holder.ratingTime.setText(formattedDate);
        holder.review.setText(ratingModel.getReview());
        holder.ratingBar.setRating(ratingModel.getRating());
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public void setItems(ArrayList<RatingModel> data)
    {
        ratings.clear();
        ratings.addAll(data);
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImg;
        TextView userName, ratingTime;
        TextView review;
        AppCompatRatingBar ratingBar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profileImg);
            userName = itemView.findViewById(R.id.userName);
            ratingTime = itemView.findViewById(R.id.rating_time);
            review = itemView.findViewById(R.id.review);
            ratingBar = itemView.findViewById(R.id.user_rating);
        }
    }
}
