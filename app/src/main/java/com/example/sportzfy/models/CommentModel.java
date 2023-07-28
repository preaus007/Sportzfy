package com.example.sportzfy.models;

public class CommentModel {
   private String commentedBy;
   private long commentedAt;
   private String comment;

   public CommentModel() {
   }

   public String getCommentedBy() {
      return commentedBy;
   }

   public void setCommentedBy(String commentedBy) {
      this.commentedBy = commentedBy;
   }

   public long getCommentedAt() {
      return commentedAt;
   }

   public void setCommentedAt(long commentedAt) {
      this.commentedAt = commentedAt;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }
}
