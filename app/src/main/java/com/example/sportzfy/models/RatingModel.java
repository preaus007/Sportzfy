package com.example.sportzfy.models;

public class RatingModel {
    private float rating;
    private String review;
    private String ratedBy;
    private long ratedAt;

    public RatingModel() {
    }

    public RatingModel(float rating, String review, String ratedBy, long ratedAt) {
        this.rating = rating;
        this.review = review;
        this.ratedBy = ratedBy;
        this.ratedAt = ratedAt;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String ratedBy) {
        this.ratedBy = ratedBy;
    }

    public long getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(long ratedAt) {
        this.ratedAt = ratedAt;
    }
}
