package com.longlongago.openvcall.model;

public class User {
    public User(int uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public final int uid;
    public final String name;
}
