package com.example.tatar.by.controller;

import com.example.tatar.by.dto.ReportDTO;
import com.example.tatar.by.mapper.Mapper;
import com.example.tatar.by.model.*;
import com.example.tatar.by.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Контроллер для админа", description = "Контроллер для управления книгами")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminController {

    private final ReportService reportService;
    private final PostService postService;
    private final Mapper mapper;
    private final CommentService commentService;
    private final FavoritePostsService favoritePostsService;
    private final PersonService personService;

    public AdminController(ReportService reportService, PostService postService, Mapper mapper, CommentService commentService, FavoritePostsService favoritePostsService, PersonService personService) {
        this.reportService = reportService;
        this.postService = postService;
        this.mapper = mapper;
        this.commentService = commentService;
        this.favoritePostsService = favoritePostsService;
        this.personService = personService;
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Найти все репорты")
    public List<ReportDTO> findReportedPosts() {
        return reportService.findAll()
                .stream().map(mapper::fromReport)
                .collect(Collectors.toList());
    }

    @GetMapping("/reports/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Найти репорт по идентификатору")
    public ReportDTO findReportedPostById(@PathVariable
                                              @Parameter(description = "Идентификатор репорта")
                                                      int id) {
        return mapper.fromReport(reportService.findById(id));
    }

    @GetMapping("/reports/{id}/accept-report")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Выдать репорт пользователю")
    public ResponseEntity<?> acceptReport(@PathVariable
                                                      @Parameter(description = "Идентификатор репорта")
                                                      int id) {
        Report report = reportService.findById(id);
        Post post = report.getPost();
        Person person = report.getPerson();
        Comment comment = report.getComment();
        if (post != null) {
            addReportToPost(post, report);
        } else if (person != null) {
            addReportToUser(person);
            reportService.delete(report);
        } else if (comment != null) {
            addReportToComment(comment, report);
        }
        //kafka logic
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }


    @GetMapping("/reports/{id}/reject-report")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Отклонить репорт")
    public ResponseEntity<?> rejectReport(@PathVariable
                                                      @Parameter(description = "Идентификатор репорта")
                                                      int id) {
        Report report = reportService.findById(id);
        reportService.delete(report);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    private void addReportToComment(Comment comment, Report report) {
        reportService.delete(report);
        commentService.delete(comment);
        addReportToUser(comment.getPerson());
    }

    private void addReportToPost(Post post, Report report) {
        List<FavoritePost> favoritePosts = post.getFavoritePosts();
        List<Comment> comments = post.getComments();
        deleteComments(comments);
        deleteFavoritePosts(favoritePosts);
        reportService.delete(report);
        postService.delete(post);
        addReportToUser(post.getPerson());
    }

    private void deleteComments(List<Comment> comments) {
        comments.forEach(commentService::delete);
    }

    private void deleteFavoritePosts(List<FavoritePost> favoritePosts) {
        favoritePosts.forEach(favoritePostsService::delete);
    }

    private void addReportToUser(Person person) {
        person.setReportCount(person.getReportCount() + 1);
        personService.save(person);
    }
}

