package com.example.adminofcubicle.Model;

public class Message {
    private String message,date,time,type,to,from;

    public Message() {
    }

    public Message(String message, String date, String time, String type, String to, String from) {
        this.message = message;
        this.date = date;
        this.time = time;
        this.type = type;
        this.to = to;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
