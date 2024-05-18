package com.example.tatar.by.service;

import com.example.tatar.by.model.FavoritePost;
import com.example.tatar.by.repository.FavoritePostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoritePostsService {

    private final FavoritePostsRepository favoritePostsRepository;

    @Autowired
    public FavoritePostsService(FavoritePostsRepository favoritePostsRepository) {
        this.favoritePostsRepository = favoritePostsRepository;
    }

    @Transactional
    public void save(FavoritePost favoritePost) {
        favoritePostsRepository.save(favoritePost);
    }

    @Transactional
    public void delete(FavoritePost favoritePost) {
        favoritePostsRepository.delete(favoritePost);
    }
}