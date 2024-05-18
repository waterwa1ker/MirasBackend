package com.example.tatar.by.service;

import com.example.tatar.by.model.Person;
import com.example.tatar.by.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public Optional<Person> findById(int id) { return personRepository.findById(id); }

    public List<Person> findByNameContaining(String name) { return personRepository.findByNameContaining(name); }

    @Transactional
    public void save(Person person) {
        personRepository.save(person);
    }
}