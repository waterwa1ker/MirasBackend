package com.example.tatar.by.dto;

import com.example.tatar.by.constants.PostCategory;
import com.example.tatar.by.constants.PostGenre;
import com.example.tatar.by.util.GenresConverter;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Convert;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "Сущность для постов")
public class PostDTO {

    @Schema(name = "Идентификатор поста")
    private int id;

    @Schema(name = "Заголовок поста")
    private String title;

    @Schema(name = "Содержимое поста")
    private String text;

    @Schema(name = "Время создания поста")
    private LocalDateTime createdAt;

    @Schema(name = "Количество лайков")
    private int likes;

    @Schema(name = "Редактирован ли пост")
    private boolean isEdited;

    @Schema(name = "Путь до изображения")
    private String image;

    @Schema(name = "Категория поста")
    private PostCategory category;

    @Schema(name = "Жанры поста")
    @Convert(converter = GenresConverter.class)
    private List<PostGenre> genres;

    @Schema(name = "Ссылка на пост, к которому ссылаются (репост)")
    private PostDTO parentPost;

    @Schema(name = "Комментарии к посту")
    private List<CommentDTO> comments;

    @Schema(name = "Обладатель поста")
    private PersonDTO person;

    @Schema(name = "Текст на татарском")
    private String tatarText;


    public PostDTO() {
    }

    public PostDTO(String text, LocalDateTime createdAt, int likes, PostDTO parentPost, List<CommentDTO> comments) {
        this.text = text;
        this.createdAt = createdAt;
        this.likes = likes;
        this.parentPost = parentPost;
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
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

    public PostDTO getParentPost() {
        return parentPost;
    }

    public void setParentPost(PostDTO parentPost) {
        this.parentPost = parentPost;
    }

    public String getTatarText() {
        return tatarText;
    }

    public void setTatarText(String tatarText) {
        this.tatarText = tatarText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    public PostCategory getCategory() {
        return category;
    }

    public void setCategory(PostCategory category) {
        this.category = category;
    }

    public List<PostGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<PostGenre> genres) {
        this.genres = genres;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
