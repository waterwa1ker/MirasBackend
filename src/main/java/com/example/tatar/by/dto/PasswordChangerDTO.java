package com.example.tatar.by.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сущность для смены пароля")
public class PasswordChangerDTO {

    @Schema(description = "Почта пользователя")
    private String email;

    @Schema(description = "Старый пароль пользователя")
    private String currentPassword;

    @Schema(description = "Новый пароль пользователя")
    private String newPassword;

    public PasswordChangerDTO(String email, String currentPassword, String newPassword) {
        this.email = email;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public PasswordChangerDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}