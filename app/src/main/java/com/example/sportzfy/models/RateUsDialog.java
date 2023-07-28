package com.example.sportzfy.models;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.example.sportzfy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import es.dmoral.toasty.Toasty;


public class RateUsDialog extends Dialog {

    private float userRate = 0;
    private String userReview = "";

    public RateUsDialog(@NonNull Context context) {
        super(context);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_us_dialog_layout);

        final AppCompatButton rateNowBtn = findViewById(R.id.submit);
        final AppCompatButton laterBtn = findViewById(R.id.later);
        final AppCompatRatingBar ratingBar = findViewById(R.id.user_rating);
        final ImageView ratingImage = findViewById(R.id.emotions);
        final EditText review = findViewById(R.id.review);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating <= 1){
                    ratingImage.setImageResource(R.drawable.sad);
                }
                else if(rating <= 2){
                    ratingImage.setImageResource(R.drawable.confused);
                }
                else if(rating <= 3){
                    ratingImage.setImageResource(R.drawable.smile);
                }
                else if(rating <= 4){
                    ratingImage.setImageResource(R.drawable.happy);
                }
                else{
                    ratingImage.setImageResource(R.drawable.love);
                }

                animateImage(ratingImage);
                userRate = rating;
            }
        });

        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code goes here
                userRate = ratingBar.getRating();
                userReview = review.getText().toString();

                uploadInDatabase(userRate, userReview);

                ratingBar.setRating(0);
                review.setText(null);

                dismiss();
            }
        });

        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    private void uploadInDatabase(float userRate, String userReview) {

        RatingModel ratingModel = new RatingModel();

        ratingModel.setRating(userRate);
        ratingModel.setReview(userReview);
        ratingModel.setRatedBy(FirebaseAuth.getInstance().getUid());
        ratingModel.setRatedAt(new Date().getTime());

        FirebaseDatabase.getInstance().getReference()
                .child("ratings")
                .push()
                .setValue(ratingModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toasty.success(getContext(), "Thank you", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    private void animateImage(ImageView ratingImage){
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }
}
