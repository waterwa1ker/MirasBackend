package com.example.tatar.by.model;

import javax.persistence.*;

@Entity
@Table(name = "favorite_post")
public class FavoritePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public FavoritePost() {
    }

    public FavoritePost(int id, Person person, Post post) {
        this.id = id;
        this.person = person;
        this.post = post;
    }

    public FavoritePost(Person person, Post post) {
        this.person = person;
        this.post = post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
