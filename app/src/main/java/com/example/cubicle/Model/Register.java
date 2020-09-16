package com.example.cubicle.Model;

public class Register {
    private String Name;
    private String Email;
    private String Password;
    private long Id;

    public Register() {
    }

    public Register(String name, String email, String password, long id) {
        Name = name;
        Email = email;
        Password = password;
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
}
