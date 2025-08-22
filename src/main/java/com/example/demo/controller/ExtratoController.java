package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExtratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/extrato")
    public String viewExtrato(
            Model model,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<Account> contas = "ADMIN".equals(user.getType())
                ? accountService.findAll()
                : accountService.findByUser(user);

        model.addAttribute("user", user);

        UUID selectedAccountId = accountId != null ? accountId : (contas.isEmpty() ? null : contas.get(0).getId());

        List<Transaction> extrato = extratoService.getExtrato(selectedAccountId, start, end);

        model.addAttribute("extrato", extrato);
        model.addAttribute("filterableAccounts", contas);
        model.addAttribute("selectedAccountId", selectedAccountId);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "user/extrato";
    }
}
