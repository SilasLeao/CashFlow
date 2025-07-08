package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExtratoService;
import com.example.demo.service.OrcamentoService;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ExtratoService extratoService;

    @Autowired
    private OrcamentoService orcamentoService;

    @GetMapping("/")
    public String home() {
        return "layout/main";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, User user) {
        model.addAttribute("user", user);
        return "user/dashboard";
    }

    @GetMapping("/contas")
    public String listAccounts(Model model) {
        List<Account> accounts = accountService.findAll();
        model.addAttribute("accounts", accounts);
        return "user/contas";
    }

    @GetMapping("/transacoes")
    public String listTransactions(Model model) {
        List<Transaction> transactions = transactionService.findAll();
        model.addAttribute("transactions", transactions);
        return "user/transacoes";
    }

    @GetMapping("/user/extrato")
    public String showExtrato(Model model) {
        List<Transaction> extrato = extratoService.getExtratoDoMes();
        model.addAttribute("extrato", extrato);
        return "user/extrato";
    }

    @GetMapping("/user/orcamento")
    public String showOrcamentoAnual(Model model) {
        var planilha = orcamentoService.getPlanilhaOrcamento();
        model.addAttribute("planilha", planilha);
        return "user/orcamento";
    }

}