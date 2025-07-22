package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.enums.MovementType;
import com.example.demo.models.transactions.Category;
import com.example.demo.models.transactions.Comment;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/transacoes")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Exibe o formulário para criar uma nova transação.
     */
    @GetMapping("/criar")
    public String showCreateForm(Model model, @AuthenticationPrincipal User user) {
        List<Account> userAccounts = accountService.findByUser(user);
        List<Category> categories = categoryService.findActiveCategories();

        model.addAttribute("transaction", new Transaction());
        model.addAttribute("movementTypes", MovementType.values());
        model.addAttribute("userAccounts", userAccounts);
        model.addAttribute("categories", categories);

        return "user/formulario-transacao";
    }

    /**
     * Salva a nova transação enviada pelo formulário.
     */
    @PostMapping("/criar")
    public String createTransaction(@ModelAttribute("transaction") Transaction transaction,
                                    @RequestParam("accountId") UUID accountId,
                                    @RequestParam("categoryId") UUID categoryId) {

        Account selectedAccount = accountService.findByIdOrThrow(accountId);
        Category selectedCategory = categoryService.findByIdOrThrow(categoryId);

        transaction.setAccount(selectedAccount);
        transaction.setCategory(selectedCategory);

        transactionService.save(transaction);
        return "redirect:/transacoes";
    }


    /**
     * Exibe o formulário para editar uma transação existente.
     */
    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID da Transação inválido:" + id));

        List<Account> userAccounts = accountService.findByUser(user);
        List<Category> categories = categoryService.findActiveCategories();


        if (transaction.getComment() == null) {
            transaction.setComment(new Comment());
        }

        model.addAttribute("transaction", transaction);
        model.addAttribute("movementTypes", MovementType.values());
        model.addAttribute("userAccounts", userAccounts);
        model.addAttribute("categories", categories);
        
        return "user/formulario-transacao";
    }

    /**
     * Salva as alterações da transação editada.
     */
     @PostMapping("/{id}/editar")
    public String updateTransaction(@PathVariable UUID id,
                                    @ModelAttribute("transaction") Transaction transaction,
                                    @RequestParam("accountId") UUID accountId,
                                    @RequestParam("categoryId") UUID categoryId) {
        transaction.setId(id);

        Account selectedAccount = accountService.findByIdOrThrow(accountId);
        Category selectedCategory = categoryService.findByIdOrThrow(categoryId);

         transaction.setAccount(selectedAccount);
         transaction.setCategory(selectedCategory);

        transactionService.save(transaction);
        return "redirect:/transacoes";
    }
    
    /**
     * Lista as transações.
     * - Se o usuário for ADMIN, mostra todas as transações.
     * - Se for um usuário NORMAL, mostra apenas as suas próprias transações.
     */
    @GetMapping
    public String listTransactions(Model model,
                                @AuthenticationPrincipal User user,
                                @RequestParam(required = false) UUID accountId) {

        List<Account> filterableAccounts;
        List<Transaction> transacoes;

        // 1. Determina quais contas o usuário pode usar para filtrar
        if ("ADMIN".equals(user.getType())) {
            filterableAccounts = accountService.findAll(); // Admin pode filtrar por qualquer conta
        } else {
            filterableAccounts = accountService.findByUser(user); // Usuário normal só pode filtrar por suas contas
        }

        UUID finalSelectedAccountId = accountId;

        // 2. Determina quais transações mostrar na tabela
        if (accountId != null) {
            // Se um filtro de conta foi aplicado
            // Adicional de segurança: um usuário normal só pode filtrar por suas próprias contas
            if ("NORMAL".equals(user.getType())) {
                // A lambda continua usando 'accountId', que agora não será mais modificado.
                boolean accountOwnedByUser = filterableAccounts.stream().anyMatch(acc -> acc.getId().equals(accountId));
                if (!accountOwnedByUser) {
                // Se tentar filtrar por conta de outro, ignora o filtro e mostra todas as suas transações.
                transacoes = transactionService.findByUser(user);
                
                finalSelectedAccountId = null; // Limpa o ID para o dropdown não mostrar seleção inválida
                } else {
                transacoes = transactionService.findByAccountId(accountId);
                }
            } else { // Admin pode filtrar por qualquer conta
                transacoes = transactionService.findByAccountId(accountId);
            }

        } else {
            // Se nenhum filtro foi aplicado, mostra a visão padrão
            if ("ADMIN".equals(user.getType())) {
                transacoes = transactionService.findAll();
            } else {
                transacoes = transactionService.findByUser(user);
            }
        }


        model.addAttribute("user", user);
        model.addAttribute("transacoes", transacoes);
        model.addAttribute("filterableAccounts", filterableAccounts);
        model.addAttribute("selectedAccountId", finalSelectedAccountId);

        return "user/transacoes";
    }

}