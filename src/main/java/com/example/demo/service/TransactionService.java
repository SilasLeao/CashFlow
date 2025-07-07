package com.example.demo.service;

import com.example.demo.models.transactions.Transaction;
import com.example.demo.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction findById(UUID id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        transactionRepository.deleteById(id);
    }
}
