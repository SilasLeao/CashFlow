package com.example.demo.service;

import com.example.demo.models.transactions.Transaction;
import com.example.demo.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ExtratoService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getExtrato(UUID accountId, LocalDate start, LocalDate end) {
        if (accountId == null) return List.of();

        LocalDate firstDay = (start != null) ? start : LocalDate.now().withDayOfMonth(1);
        LocalDate lastDay = (end != null) ? end : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());


        Date inicio = Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fim = Date.from(lastDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return transactionRepository.findByAccount_IdAndDateBetween(accountId, inicio, fim);
    }
}