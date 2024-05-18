package com.example.tatar.by.service;

import com.example.tatar.by.model.Comment;
import com.example.tatar.by.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Comment comment) { commentRepository.delete(comment);}

    public Comment findById(int id) {
        return commentRepository.findById(id).orElse(null);
    }
}