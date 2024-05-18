package com.example.tatar.by.controller;

import com.example.tatar.by.dto.AuthDTO;
import com.example.tatar.by.dto.PasswordChangerDTO;
import com.example.tatar.by.dto.RegistrationCodeDTO;
//import com.example.tatar.by.kafka.KafkaProducer;
import com.example.tatar.by.mapper.Mapper;
import com.example.tatar.by.model.Person;
import com.example.tatar.by.model.RegistrationCode;
import com.example.tatar.by.security.JwtTokenProvider;
import com.example.tatar.by.service.PersonService;
import com.example.tatar.by.service.RegistrationCodeService;
import com.example.tatar.by.service.RegistrationService;
import com.example.tatar.by.util.RandomCodeGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Контроллер аутентификации", description = "Контроллер для работы с аутентификацией")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthController {

    private final String REGISTRATION_TOPIC = "REGISTRATION";

    private final RegistrationService registrationService;
    private final PersonService personService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    private final RegistrationCodeService registrationCodeService;
    //private final KafkaProducer kafkaProducer;
    private final Mapper mapper;

    @Autowired
    public AuthController(RegistrationService registrationService, PersonService personService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ModelMapper modelMapper, RegistrationCodeService registrationCodeService/*, KafkaProducer kafkaProducer*/,Mapper mapper) {
        this.registrationService = registrationService;
        this.personService = personService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
        this.registrationCodeService = registrationCodeService;
        //this.kafkaProducer = kafkaProducer;
        this.mapper = mapper;
    }

    @PostMapping("/registration")
    @Operation(
            summary = "Регистрация пользователя",
            description = "Регистрация пользователя через почту с последующим отправлением кода"
    )
    public ResponseEntity<?> register(@RequestBody @Parameter(description = "Объект пользователя") @Valid AuthDTO authDTO,
                                      BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Invalid fields", HttpStatus.BAD_REQUEST);
        }
        String email = authDTO.getEmail();
        Person person = mapper.toPerson(authDTO);
        registrationService.register(person);
        return getTokenByEmail(email, person);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Идентификация пользователя",
            description = "Идентификация пользователя через почту и пароль"
    )
    public ResponseEntity<?> authenticate(@RequestBody @Parameter(description = "Объект пользователя (пароль + почта)") AuthDTO authDTO) {
        try{
            String email = authDTO.getEmail().toLowerCase();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, authDTO.getPassword()));
            Person person = personService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
            return getTokenByEmail(email, person);
        } catch (AuthenticationException e){
            return new ResponseEntity<>("Invalid email or password", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/registration/check-code")
    @Operation(summary = "Проверка регистрационного кода")
    public ResponseEntity<?> checkRegistrationCode(@RequestBody @Valid
                                                               @Parameter(name = "Регистрационный код + почта пользователя")
                                                               RegistrationCodeDTO registrationCodeDTO){
        String email = registrationCodeDTO.getEmail();
        RegistrationCode registrationCode = registrationCodeService.findByEmail(email).get();
        if (registrationCode.getCode().equals(registrationCodeDTO.getCode())) {
            registrationCodeService.delete(registrationCode);
            return new ResponseEntity<>("ok", HttpStatus.OK);
        }
        return new ResponseEntity<>("Code doesn't equal", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/registration/send-code")
    @Operation(summary = "Отправка регистрационного кода")
    public ResponseEntity<?> sendRegistrationCode(@RequestBody @Valid
                                                              @Parameter(name = "Почта пользователя")
                                                              AuthDTO authDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String email = authDTO.getEmail();
        Optional<RegistrationCode> optionalRegistrationCode = registrationCodeService.findByEmail(email);
        if (optionalRegistrationCode.isPresent()) {
            return new ResponseEntity<>("Code was delivered, check your email", HttpStatus.BAD_REQUEST);
        }
        Optional<Person> optionalPerson = personService.findByEmail(email);
        if (optionalPerson.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        saveRegistrationCode(email);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Смена пароля (забыл пароль)")
    public ResponseEntity<?> changePassword(@RequestBody
                                            @Parameter(description = "Сущность смены пароля")
                                            PasswordChangerDTO passwordChangerDTO) {
        Optional<Person> optionalPerson = personService.findByEmail(passwordChangerDTO.getEmail());
        if (optionalPerson.isEmpty()) {
            return new ResponseEntity<>("user not found!", HttpStatus.FORBIDDEN);
        }
        Person person = optionalPerson.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        person.setPassword(encoder.encode(passwordChangerDTO.getNewPassword()));
        personService.save(person);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    private void saveRegistrationCode(String email) {
        String randomCode = RandomCodeGenerator.getRandomCode();
        RegistrationCode registrationCode = new RegistrationCode();
        registrationCode.setEmail(email);
        registrationCode.setCode(randomCode);
        registrationCodeService.save(registrationCode);
        //kafkaProducer.sendMessage(convertToKafkaMessage(REGISTRATION_TOPIC, email, randomCode));
    }

    private String convertToKafkaMessage(String topic,String email, String code) {
        StringBuilder builder = new StringBuilder();
        builder.append(topic).append(";");
        builder.append(email).append(";");
        builder.append(code).append(";");
        return builder.toString();
    }

    private ResponseEntity<?> getTokenByEmail(String email, Person person) {
        String token = jwtTokenProvider.createToken(email, person.getRole().toString());
        Map<Object, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}