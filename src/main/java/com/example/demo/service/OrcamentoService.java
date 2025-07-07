package com.example.demo.service;

import com.example.demo.models.enums.Nature;
import com.example.demo.models.transactions.Category;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.repo.TransactionRepository;
import com.example.demo.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrcamentoService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Map<Nature, List<OrcamentoLinha>> getPlanilhaOrcamento() {
        Map<Nature, List<OrcamentoLinha>> resultado = new LinkedHashMap<>();

        for (Nature natureza : Nature.values()) {
            List<Category> categorias = categoryRepository.findByNatureOrderByOrderAsc(natureza);
            List<OrcamentoLinha> linhas = new ArrayList<>();

            for (Category cat : categorias) {
                double[] valoresPorMes = new double[12];
                List<Transaction> transacoes = transactionRepository.findByCategoryId(cat.getId());

                for (Transaction t : transacoes) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(t.getDate());
                    int mes = cal.get(Calendar.MONTH);
                    valoresPorMes[mes] += t.getValue();
                }

                linhas.add(new OrcamentoLinha(cat.getName(), valoresPorMes));
            }

            resultado.put(natureza, linhas);
        }

        return resultado;
    }

    public static class OrcamentoLinha {
        private String categoria;
        private double[] valoresMensais;

        public OrcamentoLinha(String categoria, double[] valoresMensais) {
            this.categoria = categoria;
            this.valoresMensais = valoresMensais;
        }

        public String getCategoria() {
            return categoria;
        }

        public double[] getValoresMensais() {
            return valoresMensais;
        }

        public double getTotal() {
            return Arrays.stream(valoresMensais).sum();
        }
    }
}
