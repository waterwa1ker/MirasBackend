package com.example.tatar.by.dto;

import com.example.tatar.by.constants.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Объект репорта")
public class ReportDTO {

    @Schema(name = "Идентификатор репорта")
    private int id;

    @Schema(name = "Причина репорта")
    private ReportReason reason;

    @Schema(name = "Пост, который был зарепорчен")
    private PostDTO post;

    @Schema(name = "Пользователь, который был зарепорчен")
    private PersonDTO person;

    @Schema(name = "Комментарий, который был зарепорчен")
    private CommentDTO comment;

    public ReportDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public PostDTO getPostDTO() {
        return post;
    }

    public void setPost(PostDTO post) {
        this.post = post;
    }

    public PostDTO getPost() {
        return post;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    public CommentDTO getComment() {
        return comment;
    }

    public void setComment(CommentDTO comment) {
        this.comment = comment;
    }
}