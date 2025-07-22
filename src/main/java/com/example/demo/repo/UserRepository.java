package com.example.demo.repo;

import com.example.demo.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    User findByLoginAndPassword(String login, String password);
    
}
