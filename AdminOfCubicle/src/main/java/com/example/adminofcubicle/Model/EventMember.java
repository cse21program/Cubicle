package com.example.adminofcubicle.Model;

public class EventMember {
    String data,time,email,phone,profile_picture,reg_type,studentId,name;

    public EventMember() {
    }

    public EventMember(String data, String time, String email, String phone, String profile_picture, String reg_type, String studentId, String name) {
        this.data = data;
        this.time = time;
        this.email = email;
        this.phone = phone;
        this.profile_picture = profile_picture;
        this.reg_type = reg_type;
        this.studentId = studentId;
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getReg_type() {
        return reg_type;
    }

    public void setReg_type(String reg_type) {
        this.reg_type = reg_type;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
