package com.example.tatar.by.service;

import com.example.tatar.by.constants.PostGenre;
import com.example.tatar.by.model.Person;
import com.example.tatar.by.model.Post;
import com.example.tatar.by.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List<Post> findByPerson(Person person) {
        return postRepository.findByPerson(person);
    }

    public List<Post> findByGenres(List<PostGenre> genres) {
        return postRepository.findByGenresIn(genres);
    }

    public List<Post> findByTitleContains(String title) {
        return postRepository.findByTitleContainsIgnoreCase(title);
    }

    public Post findById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Post post) {
        postRepository.save(post);
    }

    @Transactional
    public void delete(Post post) { postRepository.delete(post);}
}