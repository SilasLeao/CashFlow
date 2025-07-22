package com.example.demo.service;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.users.User;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    // Verifica se já existe uma conta com o número informado
    public boolean existsByNumber(String number) {
        return accountRepository.existsByNumber(number);
    }

    // Retorna todas as contas cadastradas
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    // Retorna as contas de um usuário específico
    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    // Salva uma nova conta ou atualiza uma existente
    public Account save(Account account) {
        if (account.getUser() == null) {
            throw new IllegalArgumentException("A conta deve estar associada a um usuário.");
        }
        User user = userRepository.findById(account.getUser().getId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + account.getUser().getId()));
        
        account.setUser(user);
        
        return accountRepository.save(account);
    }

    // Busca uma conta pelo ID, lança exceção se não encontrar
    public Account findByIdOrThrow(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + id));
    }
}