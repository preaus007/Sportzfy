package com.example.sportzfy.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sportzfy.R;
import com.example.sportzfy.adapter.RatingAdapter;
import com.example.sportzfy.models.RateUsDialog;
import com.example.sportzfy.models.RatingModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RateUsFragment extends Fragment {

    private FirebaseDatabase database;

    Button prevBtn, nextBtn;
    TextView pageNumberTextView;

    int pageNumber = 1;
    String key = null, startKey = null;

    RecyclerView ratingRV;
    ArrayList<RatingModel> ratings;

    RatingAdapter ratingAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rate_us, container, false);

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.star);
        ratingRV = view.findViewById(R.id.rating_RV);
        prevBtn = view.findViewById(R.id.prev_button);
        pageNumberTextView = view.findViewById(R.id.page_number);
        nextBtn = view.findViewById(R.id.next_button);

        database = FirebaseDatabase.getInstance();

        RateUsDialog dialog = new RateUsDialog(requireContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        dialog.setCancelable(false);

        lottieAnimationView.setOnClickListener(v -> dialog.show());

        ratings = new ArrayList<>();

        ratingAdapter = new RatingAdapter(getContext(), ratings);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ratingRV.setLayoutManager(layoutManager);
        ratingRV.addItemDecoration(new DividerItemDecoration(ratingRV.getContext(), DividerItemDecoration.VERTICAL));
        //        ratingRV.setNestedScrollingEnabled(false);
        ratingRV.setAdapter(ratingAdapter);

        loadData();

        nextBtn.setOnClickListener(v -> {
            pageNumber++;
            loadData();
        });

        prevBtn.setOnClickListener(v -> {
            pageNumber--;
            loadPrevData();
        });

        return view;
    }

    private void loadData() {
        int contentSize = 5;

        DatabaseReference reference = database.getReference("ratings");

        Query query;

        if(key == null) {
            query = reference.orderByKey().limitToFirst(contentSize);
        } else {
            query = reference.orderByKey().startAfter(key).limitToFirst(contentSize);
        }

        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<RatingModel> feedbackList = new ArrayList<>();
                for(DataSnapshot feedbackSnap : snapshot.getChildren()) {
                    RatingModel feedback = feedbackSnap.getValue(RatingModel.class);
                    if(feedbackList.isEmpty()) {
                        startKey = feedbackSnap.getKey();
                    }
                    feedbackList.add(feedback);
                    key = feedbackSnap.getKey();
                }

//                Toast.makeText(getContext(), feedbackList.size()+" ", Toast.LENGTH_SHORT).show();

                pageNumberTextView.setText(pageNumber+"");

                if(pageNumber == 1) {
                    prevBtn.setEnabled(false);
                } else {
                    prevBtn.setEnabled(true);
                }

                if(feedbackList.size() >= contentSize) {
                    nextBtn.setEnabled(true);
                } else {
                    nextBtn.setEnabled(false);
                }

                ratingAdapter.setItems(feedbackList);
                ratingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadPrevData() {
        int contentSize = 5;
        DatabaseReference reference = database.getReference("ratings");

        Query query;
        if(key == null) {
            query = reference.orderByKey().limitToFirst(contentSize);
        } else {
            query = reference.orderByKey().endBefore(startKey).limitToLast(contentSize);
        }

        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<RatingModel> feedbackList = new ArrayList<>();
                for(DataSnapshot feedbackSnap : snapshot.getChildren()) {
                    RatingModel feedback = feedbackSnap.getValue(RatingModel.class);
                    if(feedbackList.isEmpty()) {
                        startKey = feedbackSnap.getKey();
                    }
                    feedbackList.add(feedback);
                    key = feedbackSnap.getKey();
                }
                pageNumberTextView.setText(pageNumber+"");

//                Toast.makeText(getContext(), feedbackList.size()+" ", Toast.LENGTH_SHORT).show();

                if(pageNumber == 1) {
                    prevBtn.setEnabled(false);
                } else {
                    prevBtn.setEnabled(true);
                }

                if(feedbackList.size() < contentSize) {
                    nextBtn.setEnabled(false);
                } else {
                    nextBtn.setEnabled(true);
                }

                ratingAdapter.setItems(feedbackList);
                ratingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}