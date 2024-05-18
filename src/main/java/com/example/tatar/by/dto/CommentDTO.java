package com.example.tatar.by.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(name = "Объект комментария")
public class CommentDTO {

    @Schema(name = "Идентификатор комментария")
    private int id;

    @Schema(name = "Текст комментария")
    private String text;

    @Schema(name = "Количество лайков")
    private int likes;

    @Schema(name = "Время создания комментария ")
    private LocalDateTime createdAt;

    @Schema(name = "Обладатель комментария")
    private PersonDTO person;

    public CommentDTO() {
    }

    public CommentDTO(String text, int likes) {
        this.text = text;
        this.likes = likes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
