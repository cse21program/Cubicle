package com.example.cubicle.Model;

public class DetailOfClubs {
    String Name,Mission,Phone,Email,Website,Founded;

    public DetailOfClubs(String name, String mission, String phone, String email, String website, String founded) {
        Name = name;
        Mission = mission;
        Phone = phone;
        Email = email;
        Website = website;
        Founded = founded;
    }

    public DetailOfClubs() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMission() {
        return Mission;
    }

    public void setMission(String mission) {
        Mission = mission;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getFounded() {
        return Founded;
    }

    public void setFounded(String founded) {
        Founded = founded;
    }
}
