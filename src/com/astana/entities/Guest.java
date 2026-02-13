package com.astana.entities;

public class Guest {
    private int id;
    private String name;
    private String email;

    public Guest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
}
