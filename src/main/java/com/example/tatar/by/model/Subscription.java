package com.example.tatar.by.model;

import javax.persistence.*;

@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private Person subscriber;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Person subscription;

    public Subscription() {
    }

    public Subscription(int id, Person subscriber, Person subscription) {
        this.id = id;
        this.subscriber = subscriber;
        this.subscription = subscription;
    }

    public Subscription(Person subscriber, Person subscription) {
        this.subscriber = subscriber;
        this.subscription = subscription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Person subscriber) {
        this.subscriber = subscriber;
    }

    public Person getSubscription() {
        return subscription;
    }

    public void setSubscription(Person subscription) {
        this.subscription = subscription;
    }
}
