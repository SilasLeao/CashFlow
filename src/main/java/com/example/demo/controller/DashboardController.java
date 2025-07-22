package com.example.demo.controller;

import com.example.demo.models.enums.Nature;
import com.example.demo.models.transactions.Category;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.repo.CategoryRepository;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class DashboardController { 

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExtratoService extratoService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal User user) {
        // O Spring Security já garante que apenas usuários autenticados cheguem aqui
        if ("NORMAL".equals(user.getType())) {
            return "redirect:/contas";
        }

        model.addAttribute("user", user);

        // Se o usuário for ADMIN, busca usuários e categorias adicionais.
        // A autorização para acessar esta URL é feita no WebSecurityConfig (ex: anyRequest().authenticated()).
        // Esta verificação interna é para lógica de negócio, como exibir dados diferentes.
        if ("ADMIN".equals(user.getType())) {
            List<User> usuarios = userService.findAll();
            model.addAttribute("usuarios", usuarios);
            List<Category> categorias = categoryService.findAll();
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
            RedirectAttributes attr,
            @AuthenticationPrincipal User user // Usuário logado injetado aqui
    ) {
        // Verificação de permissão interna: Apenas ADMIN pode criar categorias
        // para mensagens de erro personalizadas.
        if (!"ADMIN".equals(user.getType())) {
            attr.addFlashAttribute("mensagemErro", "Você não tem permissão para criar categorias.");
            return "redirect:/dashboard";
        }

        Nature naturezaEnum;
        try {
            naturezaEnum = Nature.valueOf(nature.toUpperCase());
        } catch (IllegalArgumentException e) {
            attr.addFlashAttribute("mensagemErro", "Natureza inválida");
            return "redirect:/dashboard";
        }

        Category newCategory = new Category();
        newCategory.setName(name);
        if (categoryService.existsByName(newCategory.getName())) {
            attr.addFlashAttribute("error", "Já existe uma categoria com esse nome.");
            return "redirect:/dashboard";
        }
        newCategory.setNature(naturezaEnum);
        newCategory.setOrderIndex(orderIndex);
        newCategory.setActive(active);

        categoryRepository.save(newCategory);

        attr.addFlashAttribute("mensagemSucesso", "Categoria criada com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/editarCategoria/{id}")
    public String editarCategoriaForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);

        System.out.println("teste get"); 
        Category categoria = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        List<Category> categorias = categoryRepository.findAll();

        categorias.forEach(cat -> cat.setEditing(cat.getId().equals(id)));

        model.addAttribute("categorias", categorias);
        return "user/dashboard"; 
    }

    @PostMapping("/dashboard/editarCategoria/{id}")
    public String salvarCategoriaEditada(@PathVariable UUID id,
                                         @RequestParam String name,
                                         @RequestParam String nature,
                                         @RequestParam Integer orderIndex,
                                         @RequestParam Boolean active,
                                         RedirectAttributes attr,
                                         @AuthenticationPrincipal User user) { // Usuário logado injetado aqui

        // Verificação de permissão interna.
        if (!"ADMIN".equals(user.getType())) {
            attr.addFlashAttribute("mensagemErro", "Você não tem permissão para editar categorias.");
            return "redirect:/dashboard";
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
    public String bloquearUsuario(@PathVariable UUID id, RedirectAttributes attr, @AuthenticationPrincipal User admin) {
        // Verificação de permissão interna.
        if (!"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Acesso negado para bloquear usuários.");
            return "redirect:/dashboard";
        }

        User user = userService.findByIdOrThrow(id);

        if (user != null && !"ADMIN".equals(user.getType())) {
            user.setBlocked(true);
            userService.saveUser(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário bloqueado com sucesso!");
        } else if (user != null && "ADMIN".equals(user.getType())) {
             attr.addFlashAttribute("mensagemErro", "Não é possível bloquear outro administrador.");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluirUsuario(@PathVariable UUID id, RedirectAttributes attr, @AuthenticationPrincipal User admin) {
        // Verificação de permissão interna.
        if (!"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Acesso negado para excluir usuários.");
            return "redirect:/dashboard";
        }

        User user = userService.findByIdOrThrow(id);

        if (user != null && !"ADMIN".equals(user.getType())) {
            userService.delete(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        } else if (user != null && "ADMIN".equals(user.getType())) {
             attr.addFlashAttribute("mensagemErro", "Não é possível excluir outro administrador.");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable UUID id, RedirectAttributes attr, @AuthenticationPrincipal User admin) {
        // Verificação de permissão interna.
        if (!"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Acesso negado para desbloquear usuários.");
            return "redirect:/dashboard";
        }

        User user = userService.findByIdOrThrow(id);

        if (user != null && !"ADMIN".equals(user.getType())) {
            user.setBlocked(false);
            userService.saveUser(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário desbloqueado com sucesso!");
        } else if (user != null && "ADMIN".equals(user.getType())) {
             attr.addFlashAttribute("mensagemErro", "Não é possível desbloquear outro administrador.");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/cadastrar")
    public String cadastrarUsuario(@RequestParam String name,
                                   @RequestParam String login,
                                   @RequestParam String password,
                                   @RequestParam String type,
                                   RedirectAttributes attr,
                                   @AuthenticationPrincipal User admin) {
        if (!"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Você não tem permissão para cadastrar usuários.");
            return "redirect:/dashboard";
        }

        if (!type.equals("NORMAL") && !type.equals("ADMIN")) {
            attr.addFlashAttribute("mensagemErro", "Tipo de usuário inválido.");
            return "redirect:/dashboard";
        }

        // Verifica se o login já existe usando o service
        if (userService.loginExists(login)) {
            attr.addFlashAttribute("mensagemErro", "Login já está em uso.");
            return "redirect:/dashboard";
        }

        // Cria e salva usuário usando o service
        User novoUsuario = new User();
        novoUsuario.setName(name);
        novoUsuario.setLogin(login);
        novoUsuario.setPassword(password); // será codificado no service
        novoUsuario.setType(type);
        novoUsuario.setBlocked(false);

        userService.saveUser(novoUsuario);

        attr.addFlashAttribute("mensagemSucesso", "Usuário cadastrado com sucesso!");
        return "redirect:/dashboard";
    }


    @GetMapping("/extrato")
    public String showExtrato(Model model, @AuthenticationPrincipal User user) { 
        model.addAttribute("user", user); 
        List<Transaction> extrato = extratoService.getExtratoDoMes();
        model.addAttribute("extrato", extrato);
        return "user/extrato"; 
    }

    @GetMapping("/orcamento")
    public String showOrcamentoAnual(Model model, @AuthenticationPrincipal User user) { 
        model.addAttribute("user", user);
        var planilha = orcamentoService.getPlanilhaOrcamento();
        model.addAttribute("planilha", planilha);
        return "user/orcamento";
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}