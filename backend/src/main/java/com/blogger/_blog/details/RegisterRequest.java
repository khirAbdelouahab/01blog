package com.blogger._blog.details;

import com.blogger._blog.enums.UserRole;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private UserRole role;


    public RegisterRequest(String _name,String _email,String _password,UserRole _userRole) {
        name = _name;
        email = _email;
        password = _password;
        role = _userRole;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
