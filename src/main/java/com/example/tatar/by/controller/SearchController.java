package com.example.tatar.by.controller;

import com.example.tatar.by.dao.PostDAO;
import com.example.tatar.by.dto.PersonDTO;
import com.example.tatar.by.dto.PostDTO;
import com.example.tatar.by.mapper.Mapper;
import com.example.tatar.by.model.Post;
import com.example.tatar.by.service.PersonService;
import com.example.tatar.by.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Контроллер для поиска")
@CrossOrigin(origins = "http://localhost:8080")
public class SearchController {

    private final PostService postService;
    private final Mapper mapper;
    private final PersonService personService;
    private final PostDAO postDAO;

    public SearchController(PostService postService, Mapper mapper, PersonService personService, PostDAO postDAO) {
        this.postService = postService;
        this.mapper = mapper;
        this.personService = personService;
        this.postDAO = postDAO;
    }

    @GetMapping("/find-posts")
    @Operation(summary = "Поиск по заголовку постов")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public List<PostDTO> findPostsContainsQuery(@RequestParam
                                                            @Parameter(name = "Заголовок поста")
                                                            String q) {
        System.out.println(q);
        return postService.findByTitleContains(q)
                .stream().map(mapper::fromPost)
                .collect(Collectors.toList());
    }

    @GetMapping("/find-users")
    @Operation(summary = "Поиск автора")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public List<PersonDTO> findAuthors(@RequestParam
                                       @Parameter(name = "Никнейм пользователя")
                                       String u) {
        return personService.findByNameContaining(u)
                .stream().map(mapper::fromPerson)
                .collect(Collectors.toList());
    }


    @PostMapping("/find-by-genres")
    @Operation(summary = "Фильтрация по жанрам")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public List<PostDTO> findPostsByGenres(@RequestBody
                                           @Parameter(name = "Список жанров")
                                           PostDTO postDTO) throws SQLException {
        List<Post> posts = postDAO.getByGenres(postDTO.getGenres(), false);
        return posts.stream().map(mapper::fromPost)
                .collect(Collectors.toList());
    }

    @PostMapping("/find-by-genres-reverse")
    @Operation(summary = "Фильтрация по жанрам (наоборот)")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public List<PostDTO> findPostsByGenresReverse(@RequestBody
                                                  @Parameter(name = "Список жанров, которые не нужны")
                                                  PostDTO postDTO) throws SQLException {
        List<Post> posts = postDAO.getByGenres(postDTO.getGenres(), true);
        return posts.stream().map(mapper::fromPost)
                .collect(Collectors.toList());
    }
}
