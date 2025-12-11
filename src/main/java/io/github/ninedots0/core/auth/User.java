package io.github.ninedots0.core.auth;

public class User {
    private String username;
    private String password; // 不加密
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
