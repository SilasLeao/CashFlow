package com.example.demo.service;

import com.example.demo.models.transactions.Transaction;
import com.example.demo.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExtratoService {

    @Autowired
    private TransactionRepository transactionRepository;

    // Exemplo: retorna transações do mês atual (simulação simples)
    public List<Transaction> getExtratoDoMes() {
        UUID contaSimuladaId = UUID.fromString("00000000-0000-0000-0000-000000000001"); // simule ou pegue via usuário
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date inicio = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date fim = calendar.getTime();

        return transactionRepository.findByAccount_IdAndDateBetween(contaSimuladaId, inicio, fim);
    }
}
