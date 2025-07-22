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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contas")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    // Exibe a tela de listagem de contas
    @GetMapping
    public ModelAndView listAccounts(@AuthenticationPrincipal User user, ModelAndView mav) {
        mav = new ModelAndView("user/contas");

        // ADMIN vê todas as contas, usuário normal só as suas
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

        // ADMIN pode escolher outro usuário para vincular a conta
        if ("ADMIN".equals(currentUser.getType()) && userId != null) {
            User selectedUser = userService.findByIdOrThrow(userId);
            account.setUser(selectedUser);
        // Usuário comum cria conta para si mesmo
        } else {
            account.setUser(currentUser);
        }

        // Validação que impede a criação de conta com número já existente
        if (accountService.existsByNumber(account.getNumber())) {
            attr.addFlashAttribute("errorMessage", "Já existe uma conta com esse número.");
            return "redirect:/contas";
        }

        account.setFinishDay(LocalDateTime.now().plusYears(1));
        accountService.save(account);
        attr.addFlashAttribute("mensagemSucesso", "Conta criada com sucesso!");
        return "redirect:/contas";
    }

    // Exibe o formulário de edição de conta
    @GetMapping("/editar/{id}")
    public ModelAndView editarConta(@PathVariable UUID id, @AuthenticationPrincipal User user) {

        Account conta = accountService.findByIdOrThrow(id);
        ModelAndView mav = new ModelAndView("user/editar-conta");
        mav.addObject("conta", conta);
        mav.addObject("tiposConta", AccountType.values());
        return mav;
    }

    // Atualiza uma conta após o envio do formulário
    @PostMapping("/editar/{id}")
    public String atualizarConta(@PathVariable UUID id,
                                 @ModelAttribute Account contaAtualizada,
                                 @RequestParam(required = false) UUID userId,
                                 @AuthenticationPrincipal User currentUser) {

        Account conta = accountService.findByIdOrThrow(id);

        conta.setNumber(contaAtualizada.getNumber());
        conta.setDescription(contaAtualizada.getDescription());
        conta.setType(contaAtualizada.getType());

        accountService.save(conta);
        return "redirect:/contas";
    }


    @PostMapping("/excluir/{id}")
    public String excluirConta(@PathVariable UUID id,
                               @AuthenticationPrincipal User currentUser,
                               RedirectAttributes attr) {
        Account conta = accountService.findByIdOrThrow(id);

        boolean isAdmin = "ADMIN".equals(currentUser.getType());
        boolean isOwner = conta.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            attr.addFlashAttribute("errorMessage", "Você não tem permissão para excluir esta conta.");
            return "redirect:/contas";
        }

        accountService.deleteById(id);
        attr.addFlashAttribute("mensagemSucesso", "Conta excluída com sucesso!");
        return "redirect:/contas";
    }


}
