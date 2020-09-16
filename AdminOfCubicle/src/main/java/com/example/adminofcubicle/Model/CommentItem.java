package com.example.adminofcubicle.Model;

public class CommentItem {
    private String comment,date,time,profile_picture,name;

    public CommentItem() {
    }

    public CommentItem(String comment, String date, String time, String profile_picture, String name) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.profile_picture = profile_picture;
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
