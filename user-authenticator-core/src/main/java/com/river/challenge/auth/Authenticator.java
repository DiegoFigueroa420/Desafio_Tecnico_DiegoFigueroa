package com.river.challenge.auth;

public abstract class Authenticator {
    public abstract boolean authenticate(String username, String password);
}
