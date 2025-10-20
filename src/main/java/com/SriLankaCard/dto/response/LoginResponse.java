package com.SriLankaCard.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String name;
    private String email;

    public LoginResponse(String token, String name, String email) {
        this.token = token;
        this.name = name;
        this.email = email;
    }
}
