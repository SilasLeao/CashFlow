package com.example.demo.controller;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.enums.Nature;
import com.example.demo.models.transactions.Category;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.repo.CategoryRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExtratoService;
import com.example.demo.service.OrcamentoService;
import com.example.demo.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ExtratoService extratoService;

    @Autowired
    private OrcamentoService orcamentoService;

    @ModelAttribute
    public void addUserToModel(Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuario");
        if (user != null) {
            model.addAttribute("user", user);
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuario");

        if (user == null) {
            return "redirect:/";
        }

        // model.addAttribute("user", user);

        // Se for ADMIN, buscar todos os usuários
        if ("ADMIN".equals(user.getType())) {
            List<User> usuarios = userRepository.findAll();
            model.addAttribute("usuarios", usuarios);
            List<Category> categorias = categoryRepository.findAll();
            model.addAttribute("categorias", categorias);
        }

        return "user/dashboard";
    }

    @PostMapping("/dashboard/criarCategoria")
    public String criarCategoria(
            @RequestParam String name,
            @RequestParam String nature,
            @RequestParam Integer orderIndex,
            @RequestParam Boolean active,
            RedirectAttributes attr
    ) {
        // Converter String nature para enum Nature
        Nature naturezaEnum;
        try {
            naturezaEnum = Nature.valueOf(nature.toUpperCase());
        } catch (IllegalArgumentException e) {
            attr.addFlashAttribute("mensagemErro", "Natureza inválida");
            return "redirect:/dashboard";
        }

        Category novaCategoria = new Category();
        novaCategoria.setName(name);
        novaCategoria.setNature(naturezaEnum);
        novaCategoria.setOrderIndex(orderIndex);
        novaCategoria.setActive(active);

        categoryRepository.save(novaCategoria);

        attr.addFlashAttribute("mensagemSucesso", "Categoria criada com sucesso!");
        return "redirect:/dashboard";
    }


    @GetMapping("/dashboard/editarCategoria/{id}")
    public String editarCategoriaForm(@PathVariable UUID id, Model model, HttpSession session) {

        User user = (User) session.getAttribute("usuario");

        if (user == null) {
            return "redirect:/";
        }

        System.out.println("teste get");
        Category categoria = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        List<Category> categorias = categoryRepository.findAll();

        // Para controlar o modo edição, vamos marcar apenas a categoria a ser editada
        categorias.forEach(cat -> cat.setEditing(cat.getId().equals(id)));

        model.addAttribute("categorias", categorias);
        return "user/dashboard"; // ou o template onde a tabela está
    }

    @PostMapping("/dashboard/editarCategoria/{id}")
    public String salvarCategoriaEditada(@PathVariable UUID id,
                                         @RequestParam String name,
                                         @RequestParam String nature,
                                         @RequestParam Integer orderIndex,
                                         @RequestParam Boolean active,
                                         RedirectAttributes attr, HttpSession session) {

        User user = (User) session.getAttribute("usuario");

        if (user == null) {
            return "redirect:/";
        }
        Category categoria = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        categoria.setName(name);
        categoria.setNature(Nature.valueOf(nature));
        categoria.setOrderIndex(orderIndex);
        categoria.setActive(active);

        categoryRepository.save(categoria);

        attr.addFlashAttribute("mensagemSucesso", "Categoria atualizada com sucesso!");
        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/{id}/bloquear")
    public String bloquearUsuario(@PathVariable UUID id, RedirectAttributes attr, HttpSession session) {
        User admin = (User) session.getAttribute("usuario");

        if (admin == null || !"ADMIN".equals(admin.getType())) {
            return "redirect:/";
        }

        User user = userRepository.findById(id).orElse(null);

        if (user != null && !"ADMIN".equals(user.getType())) {
            user.setBlocked(true);
            userRepository.save(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário bloqueado com sucesso!");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluirUsuario(@PathVariable UUID id, RedirectAttributes attr, HttpSession session) {
        User admin = (User) session.getAttribute("usuario");

        if (admin == null || !"ADMIN".equals(admin.getType())) {
            return "redirect:/";
        }

        User user = userRepository.findById(id).orElse(null);

        if (user != null && !"ADMIN".equals(user.getType())) {
            userRepository.delete(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable UUID id, RedirectAttributes attr, HttpSession session) {
        User admin = (User) session.getAttribute("usuario");

        if (admin == null || !"ADMIN".equals(admin.getType())) {
            return "redirect:/";
        }

        User user = userRepository.findById(id).orElse(null);

        if (user != null && !"ADMIN".equals(user.getType())) {
            user.setBlocked(false);
            userRepository.save(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário desbloqueado com sucesso!");
        }

        return "redirect:/dashboard";
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

    @GetMapping("/extrato")
    public String showExtrato(Model model) {
        List<Transaction> extrato = extratoService.getExtratoDoMes();
        model.addAttribute("extrato", extrato);
        return "user/extrato";
    }

    @GetMapping("/orcamento")
    public String showOrcamentoAnual(Model model) {
        var planilha = orcamentoService.getPlanilhaOrcamento();
        model.addAttribute("planilha", planilha);
        return "user/orcamento";
    }

}