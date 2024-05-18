package com.example.tatar.by.controller;

import com.example.tatar.by.constants.PostCategory;
import com.example.tatar.by.constants.PostGenre;
import com.example.tatar.by.dao.PostDAO;
import com.example.tatar.by.dto.CommentDTO;
import com.example.tatar.by.dto.PodcastDTO;
import com.example.tatar.by.dto.PostDTO;
import com.example.tatar.by.dto.ReportDTO;
import com.example.tatar.by.mapper.Mapper;
import com.example.tatar.by.model.*;
import com.example.tatar.by.security.JwtTokenProvider;
import com.example.tatar.by.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Контроллер для новостной ленты")
@CrossOrigin(origins = "http://localhost:8080")
public class FeedController {

    private final PostService postService;
    private final PersonService personService;
    private final Mapper mapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;
    private final FavoritePostsService favoritePostsService;
    private final ReportService reportService;
    private final PostDAO postDAO;

    @Autowired
    public FeedController(PostService postService, PersonService personService, Mapper mapper, JwtTokenProvider jwtTokenProvider, CommentService commentService, FavoritePostsService favoritePostsService, ReportService reportService, PostDAO postDAO) {
        this.postService = postService;
        this.personService = personService;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.commentService = commentService;
        this.favoritePostsService = favoritePostsService;
        this.reportService = reportService;
        this.postDAO = postDAO;
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Получить все посты в ленту")
    public List<PostDTO> findAll() {
        return postService.findAll()
                .stream().map(mapper::fromPost)
                .filter(e -> e.getCategory() != PostCategory.COMMENT)
                .collect(Collectors.toList());
    }

    @GetMapping("/posts/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Получить информацию об одном посте")
    public PostDTO findById(@PathVariable
                                        @Parameter(description = "Идентификатор поста")
                                        int id) {
        return mapper.fromPost(postService.findById(id));
    }

    @PatchMapping("/posts/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Изменить количество лайков")
    public ResponseEntity<?> changeLikesCount(@PathVariable
                                                          @Parameter(description = "Идентификатор поста")
                                                          int id,
                                              @RequestBody
                                                      @Parameter(description = "Объект для изменения количества лайков")
                                                      PostDTO postDTO) {
        Post post = postService.findById(id);
        post.setLikes(postDTO.getLikes());
        postService.save(post);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @GetMapping("/posts/{id}/get-image")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Получить изображение по идентификатору поста")
    public ResponseEntity<?> getImageByPostId(@PathVariable
                                                @Parameter(name = "Идентификатор поста")
                                                            int id) {
        Post post = postService.findById(id);
        if (post == null) {
            return new ResponseEntity<>("image not found", HttpStatus.BAD_REQUEST);
        }
        String fileName = post.getImage().substring(post.getImage().lastIndexOf('/') + 1);
        return new ResponseEntity<>(fileName, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}/get-music")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Получение аудиофайла по идентификатору поста")
    public ResponseEntity<?> getMusicByPostId(@PathVariable
                                                  @Parameter(name = "Идентификатор поста")
                                                          int id) {
        Post post = postService.findById(id);
        if (post == null) {
            return new ResponseEntity<>("image not found", HttpStatus.BAD_REQUEST);
        }
        String fileName = post.getMusic().substring(post.getMusic().lastIndexOf('/') + 1);
        return new ResponseEntity<>(fileName, HttpStatus.OK);
    }



    @PostMapping("/posts/{id}/make-report")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Сделать репорт посту")
    public ResponseEntity<?> makeReport(@PathVariable
                                                    @Parameter(description = "Идентификатор поста")
                                                    int id,
                                        @RequestBody
                                                @Parameter(description = "Объект репорта (причина)")
                                                ReportDTO reportDTO) {
        Post reportedPost = postService.findById(id);
        Report report = new Report(reportDTO.getReason(), reportedPost);
        reportService.save(report);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/posts/{id}/add-comment")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Добавить комментарий к посту")
    public ResponseEntity<?> addCommentToPost(@PathVariable
                                                          @Parameter(description = "Идентификатор поста")
                                                          int id,
                                              @RequestBody @Valid
                                                      @Parameter(description = "Объект комментария")
                                                      CommentDTO commentDTO,
                                              BindingResult bindingResult,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                          @Parameter(description = "Токен пользователя")
                                                          String token) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("invalid fields", HttpStatus.BAD_REQUEST);
        }
        Person person = getPersonByToken(token);
        Post post = postService.findById(id);
        Comment comment = mapper.toComment(commentDTO);
        comment.setPost(post);
        comment.setPerson(person);
        comment.setCreatedAt(LocalDateTime.now());
        commentService.save(comment);
        setComments(post, comment);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Удалить комментария с поста")
    public ResponseEntity<?>  deleteComment(@PathVariable
                                                        @Parameter(description = "Идентификатор комментария")
                                                        int commentId,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                    @Parameter(description = "Токен пользователя")
                                                    String token) {
        Person person = getPersonByToken(token);
        List<Comment> comments = person.getComments();
        Comment deleteComment = commentService.findById(commentId);
        if (isCommentBelongsToUser(deleteComment, comments)) {
            commentService.delete(deleteComment);
            return new ResponseEntity<>("ok", HttpStatus.OK);
        }
        return new ResponseEntity<>("Operation isn't available", HttpStatus.BAD_REQUEST);
    }


    //РАЗБЕРИСЬ С ДОЧЕРНИМ ПОСТОМ
    @PostMapping("/posts/{postId}/comments/{commentId}/add-post")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Преобразовать комментарий в пост")
    public ResponseEntity<?> convertCommentToPost(@PathVariable
                                                    @Parameter(name = "Идентификатор комментария")
                                                        int commentId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                    @Parameter(name = "Токен пользователя")
                                                        String token,
                                                  @RequestBody
                                                    @Parameter(name = "Содержимое поста")
                                                        PostDTO postDTO) {
        Comment comment = commentService.findById(commentId);
        if (comment == null) {
            return new ResponseEntity<>("comment not found", HttpStatus.BAD_REQUEST);
        }
        Person person = getPersonByToken(token);
        Post post = new Post();
        post.setParentPost(createPost(comment.getText(), comment.getPerson()));
        post.setText(postDTO.getText());
        post.setPerson(person);
        post.setCreatedAt(LocalDateTime.now());
        post.setCategory(postDTO.getCategory());
        post.setGenres(postDTO.getGenres());
        postService.save(post);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/make-report")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Пожаловаться на комментарий")
    public ResponseEntity<?> makeReportToComment(@PathVariable
                                                    @Parameter(name = "Идентификатор комментария")
                                                        int commentId,
                                                 @RequestBody
                                                    @Parameter(name = "Объект репорта (причина)")
                                                        ReportDTO reportDTO) {
        Comment comment = commentService.findById(commentId);
        if (comment == null) {
            return new ResponseEntity<>("comment not found", HttpStatus.BAD_REQUEST);
        }
        Report report = new Report(reportDTO.getReason(), comment);
        reportService.save(report);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @GetMapping("/posts/{id}/make-favorite")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Добавить пост в избранное")
    public ResponseEntity<?> makeFavorite(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                      @Parameter(description = "Токен пользователя")
                                                      String token,
                                          @PathVariable
                                                  @Parameter(description = "Идентификатор поста")
                                                  int id) {
        Person person = getPersonByToken(token);
        Post post = postService.findById(id);
        FavoritePost favoritePost = new FavoritePost(person, post);
        favoritePostsService.save(favoritePost);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @GetMapping("/posts/subscriptions")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Лента новостей, на которые подписан пользователь")
    public List<PostDTO> findBySubscriptions(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                         @Parameter(description = "Токен пользователя")
                                                         String token) {
        Person person = getPersonByToken(token);
        return getPostsBySubscriptions(person);
    }

    @GetMapping("/posts/subscriptions/sort-by-date")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Лента новостей, на которые подписан пользователь, сортированные по дате")
    public List<PostDTO> findBySubscriptionsOrderByDate(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                                    @Parameter(description = "Токен пользователя")
                                                                    String token) {
        Person person = getPersonByToken(token);
        List<PostDTO> posts = getPostsBySubscriptions(person);
        return posts.
                stream().sorted(Comparator.comparing(PostDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Вывод всех избранных постов")
    public List<PostDTO> findFavoritePosts(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                       @Parameter(description = "Токен пользователя")
                                                       String token) {
        Person person = getPersonByToken(token);
        return person
                .getFavoritePosts()
                .stream().map(FavoritePost::getPost)
                .map(mapper::fromPost)
                .collect(Collectors.toList());
    }

    @GetMapping("/favorites/sort-by-date")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Вывод избранных постов, начиная с новых")
    public List<PostDTO> findFavoritePostOrderByDate(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                        @Parameter(name = "Токен пользователя")
                                                            String token) {
        Person person = getPersonByToken(token);
        return person.getFavoritePosts()
                .stream().map(FavoritePost::getPost)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(mapper::fromPost)
                .collect(Collectors.toList());
    }

    @PostMapping("/favorites/sort-by-category")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Вывод избранных постов определенной категории")
    public List<PostDTO> findFavoritePostByCategory(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                        @Parameter(name = "Токен пользователя")
                                                            String token,
                                                    @RequestBody
                                                        @Parameter(name = "Категория поста")
                                                            PostDTO postDTO) {
        Person person = getPersonByToken(token);
        PostCategory category = postDTO.getCategory();
        return person.getFavoritePosts()
                .stream().map(FavoritePost::getPost)
                .filter(e -> e.getCategory() == category)
                .map(mapper::fromPost)
                .collect(Collectors.toList());
    }

    @PostMapping("/favorites/sort-by-genres")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Вывод избранных постов определенных жанров")
    public List<PostDTO> findFavoritePostByGenres(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                      @Parameter(name = "Токен пользователя")
                                                              String token,
                                                  @RequestBody
                                                      @Parameter(name = "Жанры поста")
                                                              PostDTO postDTO) {
        Person person = getPersonByToken(token);
        List<Post> posts = person.getFavoritePosts()
                .stream().map(FavoritePost::getPost)
                .collect(Collectors.toList());
        return posts
                .stream().filter(e -> e.getGenres().containsAll(postDTO.getGenres()))
                .map(mapper::fromPost)
                .collect(Collectors.toList());
    }

    @PostMapping("/favorites/sort-by-genres-reverse")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Вывод избранных постов кроме определенных жанров")
    public List<PostDTO> findFavoritePostByGenresReverse(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                            @Parameter(name = "Токен пользователя")
                                                                String token,
                                                         @RequestBody
                                                            @Parameter(name = "Жанры поста")
                                                                 PostDTO postDTO) {
        Person person = getPersonByToken(token);
        List<Post> posts = person.getFavoritePosts()
                .stream().map(FavoritePost::getPost)
                .collect(Collectors.toList());
        return
                posts.stream().filter(e -> Collections.disjoint(e.getGenres(), postDTO.getGenres()))
                .map(mapper::fromPost)
                .collect(Collectors.toList());
    }


    private Post createPost(String text, Person person) {
        Post post = new Post();
        post.setText(text);
        post.setPerson(person);
        post.setCreatedAt(LocalDateTime.now());
        post.setCategory(PostCategory.COMMENT);
        post.setGenres(List.of(PostGenre.COMMENT));
        postService.save(post);
        return post;
    }


    private boolean isCommentBelongsToUser(Comment deleteComment, List<Comment> comments) {
        for (Comment comment : comments) {
            if (comment.equals(deleteComment)) return true;
        }
        return false;
    }

    private void setComments(Post post, Comment comment) {
        List<Comment> comments = post.getComments();
        comments.add(comment);
        postService.save(post);
    }

    private List<PostDTO> getPostsBySubscriptions(Person person) {
        List<Person> subscriptions = person.getSubscribers()
                .stream().map(Subscription::getSubscription)
                .collect(Collectors.toList());
        List<PostDTO> posts = new ArrayList<>();
        for (Person subscription : subscriptions) {
            List<PostDTO> tmp = subscription.getPosts()
                    .stream().map(mapper::fromPost)
                    .collect(Collectors.toList());
            posts.addAll(tmp);
        }
        return posts;
    }

    private Person getPersonByToken(String token) {
        String email = jwtTokenProvider.getUsername(token);
        return personService.findByEmail(email).get();
    }
}
