package com.hoon.ppmtool.exeptions;

public class UsernameExistsExceptionResponse {
    private String username;

    public UsernameExistsExceptionResponse(String message) {
        this.username = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String usernameAlreadyExists) {
        this.username = usernameAlreadyExists;
    }
}
