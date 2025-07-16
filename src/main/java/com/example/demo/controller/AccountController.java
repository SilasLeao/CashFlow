package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.enums.AccountType;
import com.example.demo.models.users.User;
import com.example.demo.service.AccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contas")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    private UserService userService;

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

        List<Account> contas = "ADMIN".equals(user.getType())
                ? accountService.findAll()
                : accountService.findByUser(user);

        mav.addObject("contas", contas);
        mav.addObject("conta", new Account());
        mav.addObject("user", user);

        if ("ADMIN".equals(user.getType())) {
            mav.addObject("usuarios", userService.findAll());
        }

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
    public String salvarConta(@ModelAttribute Account account, @RequestParam(required = false) UUID userId, @AuthenticationPrincipal User currentUser, RedirectAttributes attr) {

        if ("ADMIN".equals(currentUser.getType()) && userId != null) {
            User selectedUser = userService.findByIdOrThrow(userId);
            account.setUser(selectedUser);
        } else {
            account.setUser(currentUser);
        }

        if (accountService.existsByNumber(account.getNumber())) {
            attr.addFlashAttribute("errorMessage", "Já existe uma conta com esse número.");
            return "redirect:/contas";
        }

        account.setFinishDay(LocalDateTime.now().plusYears(1));
        accountService.save(account);
        return "redirect:/contas";
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editarConta(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        if (!"ADMIN".equals(user.getType())) {
            return new ModelAndView("redirect:/contas");
        }

        Account conta = accountService.findByIdOrThrow(id);
        ModelAndView mav = new ModelAndView("user/editar-conta");
        mav.addObject("conta", conta);
        mav.addObject("usuarios", userService.findAll());
        mav.addObject("tiposConta", AccountType.values());
        return mav;
    }

    @PostMapping("/editar/{id}")
    public String atualizarConta(@PathVariable UUID id,
                                 @ModelAttribute Account contaAtualizada,
                                 @RequestParam(required = false) UUID userId,
                                 @AuthenticationPrincipal User currentUser) {

        Account conta = accountService.findByIdOrThrow(id);

        conta.setNumber(contaAtualizada.getNumber());
        conta.setDescription(contaAtualizada.getDescription());
        conta.setType(contaAtualizada.getType());

        if ("ADMIN".equals(currentUser.getType()) && userId != null) {
            User user = userService.findByIdOrThrow(userId);
            conta.setUser(user);
        }

        accountService.save(conta);
        return "redirect:/contas";
    }


}
