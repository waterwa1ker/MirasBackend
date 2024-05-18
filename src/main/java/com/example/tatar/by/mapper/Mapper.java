package com.example.tatar.by.mapper;

import com.example.tatar.by.dto.*;
import com.example.tatar.by.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    private final ModelMapper modelMapper;

    @Autowired
    public Mapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Person toPerson(AuthDTO authDTO) {
        return modelMapper.map(authDTO, Person.class);
    }

    public PersonDTO fromPerson(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

    public PostDTO fromPost(Post post) {
        return modelMapper.map(post, PostDTO.class);
    }

    public Post toPost(PostDTO postDTO, Person person) {
        Post post = modelMapper.map(postDTO, Post.class);
        post.setPerson(person);
        return post;
    }

    public Comment toComment(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    public Report toReport(ReportDTO reportDTO) { return modelMapper.map(reportDTO, Report.class); }

    public ReportDTO fromReport(Report report) {
        return modelMapper.map(report, ReportDTO.class);
    }

}
