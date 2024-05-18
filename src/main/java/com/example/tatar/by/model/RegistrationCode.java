package com.example.tatar.by.model;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Table(name = "registration_code")
@Entity
public class RegistrationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "code")
    private String code;

    public RegistrationCode() {
    }

    public RegistrationCode(int id, @Email String email, String code) {
        this.id = id;
        this.email = email;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
