package com.example.demo.service;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.users.User;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // 👈 Importe a classe UUID

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
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

    /**
     * 👇 MÉTODO NOVO ADICIONADO AQUI 👇
     * Busca uma conta pelo ID ou lança uma exceção se não for encontrada.
     * @param id O UUID da conta.
     * @return A entidade Account encontrada.
     * @throws RuntimeException se a conta não for encontrada.
     */
    public Account findByIdOrThrow(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + id));
    }
}