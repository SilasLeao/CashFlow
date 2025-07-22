package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.models.users.User;
import com.example.demo.repo.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User isValid(User user) {
        
        if (user == null || user.getLogin() == null || user.getPassword() == null) {
            return null;
        }
        return userRepository.findByLogin(user.getLogin())
        .filter(dbUser -> passwordEncoder.matches(user.getPassword(), dbUser.getPassword()))
        .orElse(null);
    }
}
