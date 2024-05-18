package com.example.tatar.by.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Объект подкаста")
public class PodcastDTO {

    @Schema(name = "Идентификатор подкаста")
    private int id;

    @Schema(name = "Текст к подкасту")
    private String text;

    public PodcastDTO() {
    }

    public PodcastDTO(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
