package com.example.tatar.by.repository;

import com.example.tatar.by.constants.PostGenre;
import com.example.tatar.by.model.Person;
import com.example.tatar.by.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByPerson(Person person);
    List<Post> findByTitleContainsIgnoreCase(String title);

    @Query("select p from Post p where p.genres in ?1")
    List<Post> findByGenresIn(List<PostGenre> genres);

}