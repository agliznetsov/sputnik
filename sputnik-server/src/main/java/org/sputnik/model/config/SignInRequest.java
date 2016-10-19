package org.sputnik.model.config;

import lombok.Data;

@Data
public class SignInRequest {
    public String username;
    public String password;
}
