package com.atinity.doct;

public class Users {

    String name;
    String email;
    String imageUrl;
    String uid;
    String status;
    String Domain;


    // Why we need empty constructor ??
    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getDomain() {
        return Domain;
    }

    public void setDomain(String domain) {
        Domain = domain;
    }

    public Users(String n, String e, String i, String id, String s, String domain) {

        this.name = n;
        this.email = e;
        this.imageUrl = i;
        this.uid = id;
        this.status = s;
        this.Domain=domain;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
