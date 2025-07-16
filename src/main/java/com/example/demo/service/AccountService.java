package com.example.demo.service;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.users.User;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public boolean existsByNumber(String number) {
        return accountRepository.existsByNumber(number);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public Account save(Account account) {
        if (account.getUser() != null) {
            Optional<User> userOptional = userRepository.findById(account.getUser().getId());
            userOptional.ifPresent(account::setUser); // garante que o user existe
        }
        return accountRepository.save(account);
    }

    public Account findByIdOrThrow(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + id));
    }
}