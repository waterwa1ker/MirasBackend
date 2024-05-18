package com.example.tatar.by.dto;

import com.example.tatar.by.constants.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;

@Schema(description = "Объект пользователя")
public class PersonDTO {

    @Schema(description = "Идентификатор пользователя")
    private int id;

    @Column(name = "name")
    @Schema(description = "Имя пользователя")
    private String name;

    @Email
    @Column(name = "email")
    @Schema(description = "Почта пользователя")
    private String email;

    @Column(name = "phone")
    @Schema(description = "Номер телефона пользователя")
    private String phone;

    @Schema(description = "Имя пользователя в телеграмме")
    private String telegramUsername;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Роль пользователя")
    private UserRole role;

    public PersonDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
