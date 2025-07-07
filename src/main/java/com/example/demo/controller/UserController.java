package com.example.demo.controller;


import com.example.demo.models.accounts.Account;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ExtratoService extratoService;

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/dashboard")
    public String showDashboard(Model model) {
        User user = getUsuarioSimulado(); // Troque por autenticação real depois
        model.addAttribute("user", user);
        return "user/dashboard";
    }

    @GetMapping("/user/contas")
    public String listAccounts(Model model) {
        List<Account> accounts = accountService.findAll();
        model.addAttribute("accounts", accounts);
        return "user/contas";
    }

    @GetMapping("/user/transacoes")
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

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.ok(createdUser);
    }
}
