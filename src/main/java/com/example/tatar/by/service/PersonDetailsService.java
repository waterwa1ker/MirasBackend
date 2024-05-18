package com.example.tatar.by.service;

import com.example.tatar.by.model.Person;
import com.example.tatar.by.repository.PersonRepository;
import com.example.tatar.by.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> optionalPerson = personRepository.findByEmail(username);
        if (optionalPerson.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }
        return new PersonDetails(optionalPerson.get());
    }
}
