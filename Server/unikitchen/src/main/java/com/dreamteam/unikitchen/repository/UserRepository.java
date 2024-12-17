package com.dreamteam.unikitchen.repository;

import com.dreamteam.unikitchen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by username
    Optional<User> findByUsername(String username);

}
