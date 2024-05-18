package com.example.tatar.by.model;

import com.example.tatar.by.constants.PostCategory;
import com.example.tatar.by.constants.PostGenre;
import com.example.tatar.by.util.GenresConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "likes")
    private int likes;

    @Column(name = "is_edited")
    private boolean isEdited;

    @Column(name = "image")
    private String image;

    @Column(name = "music")
    private String music;

    @Column(name = "tatar_text")
    private String tatarText;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @Column(name = "genres")
    @Convert(converter =     GenresConverter.class)
    private List<PostGenre> genres;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post parentPost;

    @OneToMany(mappedBy = "parentPost")
    private List<Post> childPosts;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Person person;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<FavoritePost> favoritePosts;

    @OneToMany(mappedBy = "post")
    private List<Report> reports;

    public Post() {
    }

    public Post(int id, String text, LocalDateTime createdAt, int likes, Post parentPost, List<Post> childPosts, Person person, List<Comment> comments) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.likes = likes;
        this.parentPost = parentPost;
        this.childPosts = childPosts;
        this.person = person;
        this.comments = comments;
    }

    public Post(int id, String text, LocalDateTime createdAt, int likes, boolean isEdited, PostCategory category, List<PostGenre> genres) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isEdited = isEdited;
        this.category = category;
        this.genres = genres;
    }

    public String getTatarText() {
        return tatarText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTatarText(String tatarText) {
        this.tatarText = tatarText;
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

    public Post getParentPost() {
        return parentPost;
    }

    public void setParentPost(Post parentPost) {
        this.parentPost = parentPost;
    }

    public List<Post> getChildPosts() {
        return childPosts;
    }

    public void setChildPosts(List<Post> childPosts) {
        this.childPosts = childPosts;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
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

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public PostCategory getCategory() {
        return category;
    }

    public void setCategory(PostCategory category) {
        this.category = category;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<FavoritePost> getFavoritePosts() {
        return favoritePosts;
    }

    public void setFavoritePosts(List<FavoritePost> favoritePosts) {
        this.favoritePosts = favoritePosts;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
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

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", likes=" + likes +
                ", isEdited=" + isEdited +
                ", image='" + image + '\'' +
                ", music='" + music + '\'' +
                ", tatarText='" + tatarText + '\'' +
                ", category=" + category +
                ", genres=" + genres +
                ", parentPost=" + parentPost +
                ", childPosts=" + childPosts +
                ", person=" + person +
                ", comments=" + comments +
                ", favoritePosts=" + favoritePosts +
                ", reports=" + reports +
                '}';
    }
}
