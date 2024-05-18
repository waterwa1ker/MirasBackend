package com.example.tatar.by.repository;

import com.example.tatar.by.model.FavoritePost;
import com.example.tatar.by.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritePostsRepository extends JpaRepository<FavoritePost, Integer> {

    Optional<FavoritePost> findByPerson(Person person);
}