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

// Controlador responsável por lidar com as ações do dashboard (gerenciamento de usuários e categorias)
@Controller
public class DashboardController { 

    @Autowired
    private ExtratoService extratoService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private UserService userService;

    // Exibe a tela de dashboard para usuários autenticados.
    @GetMapping("/dashboard")
    public String showDashboard(
            Model model,
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int pageCategories,
            @RequestParam(defaultValue = "0") int pageUsers) {

        if ("NORMAL".equals(user.getType())) {
            return "redirect:/contas";
        }

        model.addAttribute("user", user);

        if ("ADMIN".equals(user.getType())) {
            // Paginação de categorias
            int pageSize = 10;
            var categoriasPage = categoryService.findPaginated(pageCategories, pageSize);
            model.addAttribute("categorias", categoriasPage.getContent());
            model.addAttribute("categoriasTotalPages", categoriasPage.getTotalPages());
            model.addAttribute("categoriasCurrentPage", pageCategories);

            // Paginação de usuários
            var usuariosPage = userService.findPaginated(pageUsers, pageSize);
            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("usuariosTotalPages", usuariosPage.getTotalPages());
            model.addAttribute("usuariosCurrentPage", pageUsers);
        }

        return "user/dashboard";
    }

    // Criação de categoria nova
    @PostMapping("/dashboard/criarCategoria")
    public String criarCategoria(
            @RequestParam String name,
            @RequestParam String nature,
            @RequestParam Integer orderIndex,
            @RequestParam Boolean active,
            RedirectAttributes attr,
            @AuthenticationPrincipal User user
    ) {
        // Verificação de permissão interna
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

        categoryService.save(newCategory);

        attr.addFlashAttribute("mensagemSucesso", "Categoria criada com sucesso!");
        return "redirect:/dashboard";
    }


    // Exibe o formulário de edição de uma categoria.
    @GetMapping("/dashboard/editarCategoria/{id}")
    public String editarCategoriaForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);

        System.out.println("teste get"); 
        Category categoria = categoryService.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        List<Category> categorias = categoryService.findAll();

        categorias.forEach(cat -> cat.setEditing(cat.getId().equals(id)));

        model.addAttribute("categorias", categorias);
        return "user/dashboard"; 
    }


    // Processa a edição de uma categoria.
    @PostMapping("/dashboard/editarCategoria/{id}")
    public String salvarCategoriaEditada(@PathVariable UUID id,
                                         @RequestParam String name,
                                         @RequestParam String nature,
                                         @RequestParam Integer orderIndex,
                                         @RequestParam Boolean active,
                                         RedirectAttributes attr,
                                         @AuthenticationPrincipal User user) {

        // Verificação de permissão interna.
        if (!"ADMIN".equals(user.getType())) {
            attr.addFlashAttribute("mensagemErro", "Você não tem permissão para editar categorias.");
            return "redirect:/dashboard";
        }

        Category categoria = categoryService.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        categoria.setName(name);
        categoria.setNature(Nature.valueOf(nature));
        categoria.setOrderIndex(orderIndex);
        categoria.setActive(active);

        categoryService.save(categoria);

        attr.addFlashAttribute("mensagemSucesso", "Categoria atualizada com sucesso!");
        return "redirect:/dashboard";
    }

    // Bloqueia um usuário pelo ID.
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


    // Exclui um usuário do sistema.
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


    // Desbloqueia um usuário.
    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable UUID id, RedirectAttributes attr, @AuthenticationPrincipal User admin) {
        // Verificação de permissão interna.
        if (!"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Acesso negado para desbloquear usuários.");
            return "redirect:/dashboard";
        }

        User user = userService.findByIdOrThrow(id);

        if (user != null) {
            user.setBlocked(false);
            userService.saveUser(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário desbloqueado com sucesso!");
        }

        return "redirect:/dashboard";
    }


    // Cadastra um novo usuário.
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
        novoUsuario.setPassword(password); // A senha será criptografada no service
        novoUsuario.setType(type);
        novoUsuario.setBlocked(false);

        userService.saveUser(novoUsuario);

        attr.addFlashAttribute("mensagemSucesso", "Usuário cadastrado com sucesso!");
        return "redirect:/dashboard";
    }

    // Exibe o formulário de edição de usuário.
    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuarioForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User admin, RedirectAttributes attr) {
        if (!"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Você não tem permissão para editar usuários.");
            return "redirect:/dashboard";
        }

        User usuario = userService.findByIdOrThrow(id);

        if ("ADMIN".equals(usuario.getType()) && !"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Você não pode editar outro administrador.");
            return "redirect:/dashboard";
        }

        model.addAttribute("usuario", usuario);
        return "user/editar-user";
    }


    // Processa a edição de um usuário
    @PostMapping("/usuarios/{id}/editar")
    public String salvarEdicaoUsuario(@PathVariable UUID id,
                                      @RequestParam String name,
                                      @RequestParam String login,
                                      @RequestParam String type,
                                      @AuthenticationPrincipal User admin,
                                      RedirectAttributes attr) {
        if (!"ADMIN".equals(admin.getType())) {
            attr.addFlashAttribute("mensagemErro", "Você não tem permissão para editar usuários.");
            return "redirect:/dashboard";
        }

        User user = userService.findByIdOrThrow(id);
        user.setName(name);
        user.setLogin(login);
        user.setType(type);

        userService.saveUser(user);

        attr.addFlashAttribute("mensagemSucesso", "Usuário atualizado com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}