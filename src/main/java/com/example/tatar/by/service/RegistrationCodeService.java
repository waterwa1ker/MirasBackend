package com.example.tatar.by.service;

import com.example.tatar.by.model.RegistrationCode;
import com.example.tatar.by.repository.RegistrationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationCodeService {

    private final RegistrationCodeRepository registrationCodeRepository;

    @Autowired
    public RegistrationCodeService(RegistrationCodeRepository registrationCodeRepository) {
        this.registrationCodeRepository = registrationCodeRepository;
    }

    public Optional<RegistrationCode> findByEmail(String email) {
        return registrationCodeRepository.findByEmail(email);
    }

    @Transactional
    public void save(RegistrationCode registrationCode) {
        registrationCodeRepository.save(registrationCode);
    }

    @Transactional
    public void delete(RegistrationCode registrationCode) {
        registrationCodeRepository.delete(registrationCode);
    }
}
