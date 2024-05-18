package com.example.tatar.by.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Объект регистрации/идентификации")
public class AuthDTO {

    @Schema(description = "Почта пользователя")
    private String email;

    @Schema(description = "Пароль пользователя")
    private String password;

    @Schema(description = "Имя пользователя")
    private String name;

    @Schema(description = "Номер телефона пользователя")
    private String phone;

    public AuthDTO() {
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
