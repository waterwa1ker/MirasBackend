package com.example.tatar.by.model;

import com.example.tatar.by.constants.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usr")
@Schema(description = "Сущность пользователя")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "telegram_username")
    private String telegramUsername;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "password")
    private String password;

    @Column(name = "reportCount")
    private int reportCount;

    @OneToMany(mappedBy = "person")
    private List<Post> posts;

    @OneToMany(mappedBy = "subscriber")
    private List<Subscription> subscribers;

    @OneToMany(mappedBy = "subscription")
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "person")
    private List<FavoritePost> favoritePosts;

    @OneToMany(mappedBy = "person")
    private List<Comment> comments;

    public Person() {
    }

    public Person(int id, String name, @Email String email, String phone, String telegramUsername, UserRole role, String password, List<Post> posts, List<Subscription> subscribers, List<Subscription> subscriptions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.telegramUsername = telegramUsername;
        this.role = role;
        this.password = password;
        this.posts = posts;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
    }

    public Person(int id, String name, @Email String email, String phone, String telegramUsername, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.telegramUsername = telegramUsername;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Subscription> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Subscription> subscribers) {
        this.subscribers = subscribers;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public List<FavoritePost> getFavoritePosts() {
        return favoritePosts;
    }

    public void setFavoritePosts(List<FavoritePost> favoritePosts) {
        this.favoritePosts = favoritePosts;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}