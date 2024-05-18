package com.example.tatar.by.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Объект регистрационного кода")
public class RegistrationCodeDTO {

    @Schema(name = "Почта пользователя")
    private String email;
    @Schema(name = "Регистрационный код пользователя")
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
