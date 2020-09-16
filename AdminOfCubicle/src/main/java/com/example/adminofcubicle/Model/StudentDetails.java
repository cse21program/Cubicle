package com.example.adminofcubicle.Model;

public class StudentDetails {
    private String Name,Batch,Department,University,Phone,Email;

    public StudentDetails(String name, String batch, String department, String university, String phone, String email) {
        Name = name;
        Batch = batch;
        Department = department;
        University = university;
        Phone = phone;
        Email = email;
    }

    public StudentDetails() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getUniversity() {
        return University;
    }

    public void setUniversity(String university) {
        University = university;
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
}
