package com.example.tatar.by.controller;

import com.example.tatar.by.dto.*;
import com.example.tatar.by.mapper.Mapper;
import com.example.tatar.by.model.*;
import com.example.tatar.by.security.JwtTokenProvider;
import com.example.tatar.by.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user-profile")
@Tag(name = "Контроллер для работы с профилем пользователя", description = "Контроллер для работы с профилем пользователя (смена пароля, редактирование объекта пользователя)")
@CrossOrigin(origins = "http://localhost:8080")
public class UserProfileController {

    private static final int STARTER_LIKES_COUNT = 0;
    @Value("${upload.post.mask}")
    private String PATH_UPLOAD_POST;
    @Value("${upload.podcast.mask}")
    private String PATH_UPLOAD_PODCAST;

    private final JwtTokenProvider jwtTokenProvider;
    private final PersonService personService;
    private final PostService postService;
    private final Mapper mapper;
    private final SubscriptionService subscriptionService;
    private final ReportService reportService;
    private final TatarTranslateService tatarTranslateService;

    @Autowired
    public UserProfileController(JwtTokenProvider jwtTokenProvider, PersonService personService, PostService postService, Mapper mapper, SubscriptionService subscriptionService, ReportService reportService, TatarTranslateService tatarTranslateService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.personService = personService;
        this.mapper = mapper;
        this.postService = postService;
        this.subscriptionService = subscriptionService;
        this.reportService = reportService;
        this.tatarTranslateService = tatarTranslateService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Информация о пользователе по токену")
    public PersonDTO getPersonInformationByToken(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                             @Parameter(description = "Токен пользователя")
                                                             String token) {
        Person person = getPersonByToken(token);
        return mapper.fromPerson(person);
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Изменение информации о пользователе")
    public ResponseEntity<?> changePersonInformation(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                                 @Parameter(description = "Токен пользователя") String token,
                                                     @RequestBody @Valid
                                                     @Parameter(name = "Измененный пользователь")
                                                             PersonDTO personDTO,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Invalid fields", HttpStatus.BAD_REQUEST);
        }
        Person person = getPersonByToken(token);
        if (personDTO.getName() != null) {
            person.setName(personDTO.getName());
        }
        if (personDTO.getPhone() != null) {
            person.setPhone(personDTO.getPhone());
        }
        if (personDTO.getTelegramUsername() != null) {
            person.setTelegramUsername(personDTO.getTelegramUsername());
        }
        personService.save(person);
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(
            summary = "Информация о пользователе",
            description = "Информация о пользователе"
    )
    public PersonDTO getPersonInformation(@PathVariable
                                                      @Parameter(description = "Идентификатор пользователя")
                                                      int id) {
        Person person = personService.findById(id).get();
        return mapper.fromPerson(person);
    }

    @GetMapping("/{id}/subscribe")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Подписаться на пользователя")
    public ResponseEntity<?> subscribe(@PathVariable
                                                   @Parameter(description = "Идентификатор пользователя")
                                                   int id,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION)
                                               @Parameter(description = "Токен пользователя")
                                               String token) {
        Person person = getPersonByToken(token);
        Person subscriptionPerson = personService.findById(id).get();
        if (person.equals(subscriptionPerson)) {
            return new ResponseEntity<>("can't subscribe to yourself", HttpStatus.BAD_REQUEST);
        }
        Subscription subscription = new Subscription(person, subscriptionPerson);
        subscriptionService.save(subscription);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/{id}/make-report")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Отправить репорт пользователю")
    public ResponseEntity<?> makeReportToUser(@RequestBody
                                              @Parameter(name = "Объект репорта (причина)")
                                              ReportDTO reportDTO,
                                              @PathVariable
                                                      @Parameter(name = "Идентификатор пользователя")
                                                      int id) {
        Optional<Person> optionalPerson = personService.findById(id);
        if (optionalPerson.isEmpty()) {
            return new ResponseEntity<>("user not found!", HttpStatus.BAD_REQUEST);
        }
        Person person = optionalPerson.get();
        Report report = new Report(reportDTO.getReason(), person);
        reportService.save(report);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Смена пароля пользователя")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody
                                            @Parameter(name = "Сущность для смены пароля")
                                                    PasswordChangerDTO passwordChangerDTO,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                    @Parameter(name = "Токен пользователя")
                                                    String token) {
        Person person = getPersonByToken(token);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(passwordChangerDTO.getCurrentPassword(), person.getPassword())) {
            return new ResponseEntity<>("password not matches", HttpStatus.BAD_REQUEST);
        }
        person.setPassword(encoder.encode(passwordChangerDTO.getNewPassword()));
        personService.save(person);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Вывод постов, добавленных пользователем")
    public List<PostDTO> findPostsByPerson(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                          @Parameter(name = "Токен пользователя") String token) {
        Person person = getPersonByToken(token);
        return postService
                .findByPerson(person)
                .stream().map(mapper::fromPost)
                .collect(Collectors.toList());
    }

    @GetMapping("/posts/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Получение информации об одном посте")
    public PostDTO findById(@PathVariable
                                        @Parameter(description = "Идентификатор поста")
                                        int id) {
        return mapper.fromPost(postService.findById(id));
    }

    @PatchMapping("/posts/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Изменение информации об одном посте")
    public ResponseEntity<?> changePostInformation(@PathVariable
                                                               @Parameter(description = "Идентификатор поста")
                                                               int id,
                                                   @RequestBody
                                                           @Parameter(description = "Объект поста (текст + категории/жанры)")
                                                           PostDTO postDTO) {
        Post post = postService.findById(id);
        if (postDTO.getText() != null) {
            post.setText(postDTO.getText());
        }
        if (!post.isEdited()) {
            post.setEdited(true);
        }
        postService.save(post);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Удаление поста")
    public ResponseEntity<?> deletePost(@PathVariable
                                                    @Parameter(description = "Идентификатор поста")
                                                    int id) {
        Post post = postService.findById(id);
        postService.delete(post);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/posts/add-post")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(
            summary = "Добавление поста пользователем"
    )
    public ResponseEntity<?> addPost(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                 @Parameter(name = "Токен пользователя") String token,
                                     @RequestBody @Valid
                                             @Parameter(name = "Объект поста") PostDTO postDTO,
                                     BindingResult bindingResult) {
        String str = "нихәл, исемехә";
        System.out.println(str);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Invalid fields", HttpStatus.BAD_REQUEST);
        }
        Person person = getPersonByToken(token);
        Post post = mapper.toPost(postDTO, person);
        post.setCreatedAt(LocalDateTime.now());
        postService.save(post);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    //ДОБАВИТЬ УСЛОВИЯ
    @PostMapping("/posts/{id}/upload-image")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Сохранение изображения")
    public ResponseEntity<?> uploadImage(@PathVariable
                                            @Parameter(name = "Идентификатор поста")
                                                     int id,
                                         @RequestParam("file")
                                            @Parameter(name = "Файл")
                                                MultipartFile file){
        Post post = postService.findById(id);
        String fileName = createFileName(file, PATH_UPLOAD_POST);
        if (saveFile(fileName, file)) {
            post.setImage(fileName);
            postService.save(post);
            return new ResponseEntity<>("File saved", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to save", HttpStatus.BAD_REQUEST);
    }

    //ДОБАВИТЬ УСЛОВИЯ
    @PostMapping("/posts/{id}/upload-music")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Добавить содержимое подкаста")
    public ResponseEntity<?> uploadMusic(@PathVariable
                                                     @Parameter(name = "Идентификатор подкаста")
                                                        int id,
                                         @RequestParam("file")
                                             @Parameter(name = "Файл подкаста")
                                                MultipartFile file) {
        Post post = postService.findById(id);
        if (post == null) {
            return new ResponseEntity<>("Post not found", HttpStatus.BAD_REQUEST);
        }
        String fileName = createFileName(file, PATH_UPLOAD_PODCAST);
        if (saveFile(fileName, file)) {
            post.setMusic(fileName);
            postService.save(post);
            return new ResponseEntity<>("File saved", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to save", HttpStatus.BAD_REQUEST);
    }

    private boolean saveFile(String fileName, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = Files.createFile(Path.of(fileName));
            try (OutputStream outputStream = Files.newOutputStream(filePath)) {
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private String createFileName(MultipartFile file, String path) {
        String randomFileName = UUID.randomUUID().toString();
        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        return String.format(path + "%s.%s", randomFileName, extension);
    }


    private Person getPersonByToken(String token) {
        String email = jwtTokenProvider.getUsername(token);
        return personService.findByEmail(email).get();
    }

}