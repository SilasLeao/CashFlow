package com.example.demo.service;

import com.example.demo.dto.OrcamentoCategoriaDTO;
import com.example.demo.models.accounts.Account;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.transactions.Category;
import com.example.demo.repo.TransactionRepository;
import com.example.demo.repo.CategoryRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public OrcamentoService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<OrcamentoCategoriaDTO> gerarOrcamento(Account account, int ano) {
        List<Category> categorias = categoryRepository.findAll();

        // Lista de meses em abreviação
        List<String> meses = Arrays.stream(Month.values())
                .map(m -> m.name().substring(0,3))
                .collect(Collectors.toList());

        List<OrcamentoCategoriaDTO> orcamento = new ArrayList<>();

        for (Category cat : categorias) {
            Map<String, Double> valores = new LinkedHashMap<>();
            double total = 0.0;

            for (Month mes : Month.values()) {
                LocalDate start = LocalDate.of(ano, mes, 1);
                LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

                List<Transaction> transacoes = transactionRepository.findByAccountAndDateBetween(account, java.sql.Date.valueOf(start), java.sql.Date.valueOf(end));

                double somaMes = transacoes.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(cat.getId()))
                        .mapToDouble(Transaction::getValue)
                        .sum();
                valores.put(mes.name().substring(0,3), somaMes);
                total += somaMes;
            }

            OrcamentoCategoriaDTO dto = new OrcamentoCategoriaDTO();
            dto.setName(cat.getName());
            dto.setNatureza(cat.getNatureza());
            dto.setValores(valores);
            dto.setTotal(total);

            orcamento.add(dto);
        }

        // Ordenar por natureza e campo ordem
        orcamento.sort(Comparator.comparing(OrcamentoCategoriaDTO::getNatureza)
                .thenComparing(c -> {
                    Optional<Category> category = categorias.stream().filter(cat -> cat.getName().equals(c.getName())).findFirst();
                    return category.map(Category::getOrdem).orElse(0);
                }));

        return orcamento;
    }

    public List<String> getMeses() {
        return Arrays.stream(Month.values()).map(m -> m.name().substring(0,3)).collect(Collectors.toList());
    }
}