package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.enums.AccountType;
import com.example.demo.models.users.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
import org.springframework.ui.Model;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize; // -> Importação necessária
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

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
    public ModelAndView listAccounts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page) {

        ModelAndView mav = new ModelAndView("user/contas");
        int pageSize = 10;
        Page<Account> contasPage;

        if (user.isAdmin()) {
            contasPage = accountService.findPaginated(page, pageSize);
            mav.addObject("usuarios", userService.findAll());
        } else {
            contasPage = accountService.findByUserPaginated(user, page, pageSize);
        }

        mav.addObject("contas", contasPage.getContent());
        mav.addObject("contasTotalPages", contasPage.getTotalPages());
        mav.addObject("contasCurrentPage", page);
        mav.addObject("conta", new Account());
        mav.addObject("user", user);

        // 🔥 precisa adicionar sempre
        mav.addObject("tiposConta", AccountType.values());

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
    public String salvarConta(@Valid @ModelAttribute("conta") Account account,
                              BindingResult result, // Deve vir LOGO APÓS o objeto que está sendo validado
                              @RequestParam(required = false) UUID userId,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes attr,
                              Model model) { // Usado para devolver dados à view em caso de erro

        // PASSO 1: VERIFICAR SE HÁ ERROS DE VALIDAÇÃO
        if (result.hasErrors()) {
            Page<Account> contasPage = currentUser.isAdmin()
                    ? accountService.findPaginated(0, 10)
                    : accountService.findByUserPaginated(currentUser, 0, 10);

            model.addAttribute("contas", contasPage.getContent());
            model.addAttribute("contasTotalPages", contasPage.getTotalPages());
            model.addAttribute("contasCurrentPage", 0);
            model.addAttribute("tiposConta", AccountType.values());
            model.addAttribute("user", currentUser);

            // 🔥 Se for admin, precisa repopular a lista de usuários
            if (currentUser.isAdmin()) {
                model.addAttribute("usuarios", userService.findAll());
            }

            return "user/contas";
        }

        // PASSO 2: SE NÃO HOUVER ERROS, A LÓGICA ORIGINAL CONTINUA
        if (currentUser.isAdmin() && userId != null) {
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
        attr.addFlashAttribute("mensagemSucesso", "Conta criada com sucesso!");
        return "redirect:/contas";
    }

    // Exibe o formulário de edição de conta
    // REFATORADO: Adicionada verificação de permissão.
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountService.findByIdOrThrow(#id).user.id == authentication.principal.id")
    public ModelAndView editarConta(@PathVariable UUID id) {
        Account conta = accountService.findByIdOrThrow(id);
        ModelAndView mav = new ModelAndView("user/editar-conta");
        mav.addObject("conta", conta);
        mav.addObject("tiposConta", AccountType.values());
        return mav;
    }

    // Atualiza uma conta após o envio do formulário
    // REFATORADO: Adicionada verificação de permissão.
    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountService.findByIdOrThrow(#id).user.id == authentication.principal.id")
    public String atualizarConta(@PathVariable UUID id, @ModelAttribute Account contaAtualizada) {
        Account conta = accountService.findByIdOrThrow(id);
        conta.setNumber(contaAtualizada.getNumber());
        conta.setDescription(contaAtualizada.getDescription());
        conta.setType(contaAtualizada.getType());
        accountService.save(conta);
        return "redirect:/contas";
    }

    // Exclui uma conta
    // REFATORADO: Verificação de permissão movida para a anotação.
    @PostMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountService.findByIdOrThrow(#id).user.id == authentication.principal.id")
    public String excluirConta(@PathVariable UUID id, RedirectAttributes attr) {
        // A lógica manual de verificação (if !isAdmin && !isOwner) foi removida daqui.
        // O método só será executado se o usuário tiver permissão.
        accountService.deleteById(id);
        attr.addFlashAttribute("mensagemSucesso", "Conta excluída com sucesso!");
        return "redirect:/contas";
    }
}