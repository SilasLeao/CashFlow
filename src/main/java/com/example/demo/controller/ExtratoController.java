package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExtratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class ExtratoController {

    @Autowired
    private ExtratoService extratoService;

    @Autowired
    private AccountService accountService;

    // -> Permite que qualquer usuário autenticado acesse esta página.
    @GetMapping("/extrato")
    @PreAuthorize("isAuthenticated()")
    public String viewExtrato(
            Model model,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        // -> REFATORADO: Uso do método isAdmin() para maior clareza e consistência.
        // A lógica de negócio para determinar quais contas são visíveis é mantida.
        List<Account> visibleAccounts = user.isAdmin()
                ? accountService.findAll()
                : accountService.findByUser(user);

        // -> REFATORADO: Lógica de seleção de conta padrão separada para melhor legibilidade.
        UUID selectedAccountId = accountId;
        if (selectedAccountId == null && !visibleAccounts.isEmpty()) {
            selectedAccountId = visibleAccounts.get(0).getId();
        }

        // A chamada ao serviço para buscar as transações permanece a mesma.
        List<Transaction> extrato = extratoService.getExtrato(selectedAccountId, start, end);

        model.addAttribute("user", user);
        model.addAttribute("extrato", extrato);
        model.addAttribute("filterableAccounts", visibleAccounts);
        model.addAttribute("selectedAccountId", selectedAccountId);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "user/extrato";
    }
}