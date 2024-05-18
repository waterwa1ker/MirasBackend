package com.example.tatar.by.repository;

import com.example.tatar.by.model.RegistrationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationCodeRepository extends JpaRepository<RegistrationCode, Integer> {

    Optional<RegistrationCode> findByEmail(String email);

}