package com.example.demo.controller;

import com.example.demo.dto.OrcamentoCategoriaDTO;
import com.example.demo.models.accounts.Account;
import com.example.demo.models.enums.Nature;
import com.example.demo.models.users.User;
import com.example.demo.repo.TransactionRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.OrcamentoService;
import com.example.demo.repo.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public OrcamentoController(OrcamentoService orcamentoService, AccountRepository accountRepository) {
        this.orcamentoService = orcamentoService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/orcamento")
    public String telaOrcamento(@AuthenticationPrincipal User user,
                                @RequestParam(value="ano", required=false) Integer ano,
                                @RequestParam(value="accountId", required=false) UUID accountId,
                                Model model,
                                Principal principal) {

        if (ano == null) ano = java.time.LocalDate.now().getYear();

        // Buscar usuário logado
        User usuarioLogado = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Buscar contas
        List<Account> contas;
        if (usuarioLogado.isAdmin()) {
            contas = accountRepository.findAll();
        } else {
            contas = accountRepository.findByUser(usuarioLogado);
        }

        model.addAttribute("user", user);

        // Selecionar conta
        Account contaSelecionada = null;
        if (accountId != null) {
            Optional<Account> opt = accountRepository.findById(accountId);
            if (opt.isPresent() && (usuarioLogado.isAdmin() || opt.get().getUser().equals(usuarioLogado))) {
                contaSelecionada = opt.get();
            }
        }

        // Gerar orçamento
        List<OrcamentoCategoriaDTO> orcamento = contaSelecionada != null ?
                orcamentoService.gerarOrcamento(contaSelecionada, ano) : List.of();

        // Separa as listas por natureza (considerando que OrcamentoCategoriaDTO tem getNatureza())
        var entradas = orcamento.stream()
                .filter(c -> c.getNatureza() == Nature.ENTRADA)
                .toList();

        var saidas = orcamento.stream()
                .filter(c -> c.getNatureza() == Nature.SAIDA)
                .toList();

        var investimentos = orcamento.stream()
                .filter(c -> c.getNatureza() == Nature.INVESTIMENTO)
                .toList();

        // Atributos para a view
        model.addAttribute("contas", contas);
        model.addAttribute("meses", orcamentoService.getMeses());
        model.addAttribute("categoriasEntrada", entradas);
        model.addAttribute("categoriasSaida", saidas);
        model.addAttribute("categoriasInvestimento", investimentos);
        model.addAttribute("ano", ano);
        model.addAttribute("contaSelecionada", contaSelecionada);

        return "user/orcamento";
    }


}