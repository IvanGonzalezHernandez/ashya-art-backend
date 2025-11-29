package com.ashyaart.ashya_art_backend.model;

public class AuthDto {
    private String email;
    private String password;

    public AuthDto() {}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}