package com.farhetna.repository;

import com.farhetna.model.Administrator;
import com.farhetna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    Optional<Administrator> findByUser(User user);
    Optional<Administrator> findByUserId(Long userId);
}
