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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/transacoes")
@PreAuthorize("isAuthenticated()")
public class TransactionController {

    @Autowired private TransactionService transactionService;
    @Autowired private AccountService accountService;
    @Autowired private CategoryService categoryService;

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

    @PostMapping("/criar")
    @PreAuthorize("@accountService.findByIdOrThrow(#accountId).user.id == authentication.principal.id")
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

    // -> REFATORADO (Opção 2): Usa .findById(#id).get() para corrigir o erro.
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#id).get().account.user.id == authentication.principal.id")
    public String showEditForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user) {
        // Usando findByIdOrThrow do service para o corpo do método, pois ele já existe aqui.
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

    // -> REFATORADO (Opção 2): Usa .findById(#id).get() para corrigir o erro.
    @PostMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#id).get().account.user.id == authentication.principal.id")
    public String updateTransaction(@PathVariable UUID id,
                                    @ModelAttribute("transaction") Transaction formTransaction,
                                    @RequestParam("accountId") UUID accountId,
                                    @RequestParam("categoryId") UUID categoryId) {
        Transaction existing = transactionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        existing.setDescription(formTransaction.getDescription());
        existing.setValue(formTransaction.getValue());
        existing.setMovement(formTransaction.getMovement());
        existing.setDate(formTransaction.getDate());

        Account selectedAccount = accountService.findByIdOrThrow(accountId);
        Category selectedCategory = categoryService.findByIdOrThrow(categoryId);
        existing.setAccount(selectedAccount);
        existing.setCategory(selectedCategory);

        transactionService.save(existing);
        return "redirect:/transacoes";
    }
    
    @GetMapping
    public String listTransactions(Model model,
                                   @AuthenticationPrincipal User user,
                                   @RequestParam(required = false) UUID accountId) {
        List<Account> filterableAccounts = user.isAdmin()
                ? accountService.findAll()
                : accountService.findByUser(user);
        
        List<Transaction> transacoes;
        UUID finalSelectedAccountId = accountId;

        if (accountId != null) {
            boolean canAccessAccount = user.isAdmin() || filterableAccounts.stream()
                .anyMatch(acc -> acc.getId().equals(accountId));
            
            if (canAccessAccount) {
                transacoes = transactionService.findByAccountId(accountId);
            } else {
                transacoes = transactionService.findByUser(user);
                finalSelectedAccountId = null;
            }
        } else {
            transacoes = user.isAdmin()
                ? transactionService.findAll()
                : transactionService.findByUser(user);
        }

        model.addAttribute("user", user);
        model.addAttribute("transacoes", transacoes);
        model.addAttribute("filterableAccounts", filterableAccounts);
        model.addAttribute("selectedAccountId", finalSelectedAccountId);

        return "user/transacoes";
    }

    // -> REFATORADO (Opção 2): Usa .findById(#id).get() para corrigir o erro.
    @GetMapping("/{id}/comentario")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#id).get().account.user.id == authentication.principal.id")
    public String showCommentForm(@PathVariable UUID id, Model model) {
        Transaction transaction = transactionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        if (transaction.getComment() == null) {
            transaction.setComment(new Comment());
        }

        model.addAttribute("transaction", transaction);
        return "user/formulario-comentario";
    }

    // -> REFATORADO (Opção 2): Usa .findById(#id).get() para corrigir o erro.
    @PostMapping("/{id}/comentario")
    @PreAuthorize("hasRole('ADMIN') or @transactionService.findById(#id).get().account.user.id == authentication.principal.id")
    public String saveComment(@PathVariable UUID id,
                              @ModelAttribute("transaction") Transaction transaction) {
        Transaction existing = transactionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        if (existing.getComment() == null) {
            existing.setComment(new Comment());
        }
        existing.getComment().setText(transaction.getComment().getText());

        transactionService.save(existing);
        return "redirect:/transacoes";
    }
}