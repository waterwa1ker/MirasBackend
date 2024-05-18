package com.example.tatar.by.model;

import com.example.tatar.by.constants.ReportReason;

import javax.persistence.*;

@Entity
@Table(name = "report")
public class Report {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reason")
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Report() {
    }

    public Report(ReportReason reason, Post post) {
        this.reason = reason;
        this.post = post;
    }

    public Report(ReportReason reason, Person person) {
        this.reason = reason;
        this.person = person;
    }

    public Report(ReportReason reason, Comment comment) {
        this.reason = reason;
        this.comment = comment;
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
