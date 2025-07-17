package com.example.demo.service;

import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<Transaction> findByUser(User user) {
        return transactionRepository.findByAccount_User(user);
    }



    @Transactional(readOnly = true)
    public List<Transaction> findByAccountId(UUID accountId) {
        return transactionRepository.findByAccount_Id(accountId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    // Retornando Optional para mais segurança no Controller
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id);
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        if (transaction.getComment() != null && (transaction.getComment().getText() == null || transaction.getComment().getText().trim().isEmpty())) {
            transaction.setComment(null);
        }
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteById(UUID id) {
        transactionRepository.deleteById(id);
    }
}