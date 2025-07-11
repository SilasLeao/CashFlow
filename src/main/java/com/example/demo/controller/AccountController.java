package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.enums.AccountType;
import com.example.demo.models.users.User;
import com.example.demo.service.AccountService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/contas")
@CrossOrigin(origins = "*") // ajuste conforme necessário
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account savedAccount = accountService.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    // Listagem
    @GetMapping
    public ModelAndView listAccounts(@AuthenticationPrincipal User user, ModelAndView mav) {
        mav = new ModelAndView("user/contas");

        List<Account> contas;

        if ("ADMIN".equals(user.getType())) {
            contas = accountService.findAll();
        }
        else {
            // Se for usuário normal, pega só as contas dele
            contas = accountService.findByUser(user);
        }

        mav.addObject("contas", contas);
        mav.addObject("conta", new Account());

        return mav;
    }

    // Formulário de nova conta
    @GetMapping("/nova")
    public ModelAndView formNovaConta() {
        ModelAndView mav = new ModelAndView("user/nova_conta");
        mav.addObject("conta", new Account());
        mav.addObject("tiposConta", AccountType.values());
        return mav;
    }

    // Salvar nova conta
    @PostMapping("/nova")
    public String salvarConta(@ModelAttribute Account account, @AuthenticationPrincipal User user) {
        account.setUser(user);
        account.setFinishDay(LocalDateTime.now().plusYears(1));
        accountService.save(account);
        return "redirect:/contas";
    }
}
