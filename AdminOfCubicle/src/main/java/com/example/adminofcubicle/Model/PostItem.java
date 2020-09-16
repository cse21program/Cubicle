package com.example.adminofcubicle.Model;

public class PostItem {
    private String type,post,poster_name,time,date,poster_profile_picture,event_name,parent_uid,child_uid;

    public PostItem() {
    }

    public PostItem(String type, String post, String poster_name, String time, String date, String poster_profile_picture, String event_name, String parent_uid, String child_uid) {
        this.type = type;
        this.post = post;
        this.poster_name = poster_name;
        this.time = time;
        this.date = date;
        this.poster_profile_picture = poster_profile_picture;
        this.event_name = event_name;
        this.parent_uid = parent_uid;
        this.child_uid = child_uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPoster_name() {
        return poster_name;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPoster_profile_picture() {
        return poster_profile_picture;
    }

    public void setPoster_profile_picture(String poster_profile_picture) {
        this.poster_profile_picture = poster_profile_picture;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getParent_uid() {
        return parent_uid;
    }

    public void setParent_uid(String parent_uid) {
        this.parent_uid = parent_uid;
    }

    public String getChild_uid() {
        return child_uid;
    }

    public void setChild_uid(String child_uid) {
        this.child_uid = child_uid;
    }
}
