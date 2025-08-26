package com.example.demo.controller;

import com.example.demo.models.enums.Nature;
import com.example.demo.models.transactions.Category;
import com.example.demo.models.users.User;
import com.example.demo.service.*;
import jakarta.validation.Valid; // -> IMPORTADO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // -> IMPORTADO
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired private ExtratoService extratoService;
    @Autowired private CategoryService categoryService;
    @Autowired private OrcamentoService orcamentoService;
    @Autowired private UserService userService;

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String showDashboard(Model model,
                                @AuthenticationPrincipal User user,
                                @RequestParam(defaultValue = "0") int pageCategories,
                                @RequestParam(defaultValue = "0") int pageUsers) {
        
        if (!user.isAdmin()) {
            return "redirect:/contas";
        }

        model.addAttribute("user", user);

        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }
        
        // -> ALTERAÇÃO: Adiciona um objeto de usuário vazio para o formulário de cadastro.
        if (!model.containsAttribute("novoUsuario")) {
            model.addAttribute("novoUsuario", new User());
        }

        int pageSize = 10;
        var categoriasPage = categoryService.findPaginated(pageCategories, pageSize);
        model.addAttribute("categorias", categoriasPage.getContent());
        model.addAttribute("categoriasTotalPages", categoriasPage.getTotalPages());
        model.addAttribute("categoriasCurrentPage", pageCategories);

        var usuariosPage = userService.findPaginated(pageUsers, pageSize);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("usuariosTotalPages", usuariosPage.getTotalPages());
        model.addAttribute("usuariosCurrentPage", pageUsers);

        return "user/dashboard";
    }


    // -> MÉTODO COMPLETAMENTE REFATORADO PARA VALIDAÇÃO
    @PostMapping("/dashboard/criarCategoria")
    public String criarCategoria(
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            RedirectAttributes attr,
            Model model,
            @AuthenticationPrincipal User user) {

        // Verifica se há erros de validação das anotações (@NotBlank, @NotNull, etc.)
        if (result.hasErrors()) {
            // Se houver erros, repopula o model com os dados necessários para a página
            // e retorna para a view para exibir os erros.
            model.addAttribute("user", user);
            
            // Repopula os dados da paginação para a view não quebrar
            int pageSize = 10;
            var categoriasPage = categoryService.findPaginated(0, pageSize);
            model.addAttribute("categorias", categoriasPage.getContent());
            model.addAttribute("categoriasTotalPages", categoriasPage.getTotalPages());
            model.addAttribute("categoriasCurrentPage", 0);

            var usuariosPage = userService.findPaginated(0, pageSize);
            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("usuariosTotalPages", usuariosPage.getTotalPages());
            model.addAttribute("usuariosCurrentPage", 0);

            return "user/dashboard";
        }

        // Lógica de negócio original (se não houver erros de validação)
        if (categoryService.existsByName(category.getName())) {
            attr.addFlashAttribute("error", "Já existe uma categoria com esse nome.");
            return "redirect:/dashboard";
        }

        categoryService.save(category);

        attr.addFlashAttribute("mensagemSucesso", "Categoria criada com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/editarCategoria/{id}")
    public String editarCategoriaForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        Category categoria = categoryService.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        List<Category> categorias = categoryService.findAll();
        categorias.forEach(cat -> cat.setEditing(cat.getId().equals(id)));

        model.addAttribute("categorias", categorias);
        model.addAttribute("category", categoria); // 👈 ESSA LINHA RESOLVE O ERRO

        return "user/dashboard";
}

    @PostMapping("/dashboard/editarCategoria/{id}")
    public String salvarCategoriaEditada(@PathVariable UUID id,
                                         @RequestParam String name,
                                         @RequestParam String nature,
                                         @RequestParam Integer orderIndex,
                                         @RequestParam Boolean active,
                                         RedirectAttributes attr,
                                         @AuthenticationPrincipal User user) {
        Category categoria = categoryService.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        categoria.setName(name);
        categoria.setNature(Nature.valueOf(nature));
        categoria.setOrderIndex(orderIndex);
        categoria.setActive(active);
        categoryService.save(categoria);
        attr.addFlashAttribute("mensagemSucesso", "Categoria atualizada com sucesso!");
        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/{id}/bloquear")
    public String bloquearUsuario(@PathVariable UUID id, RedirectAttributes attr) {
        User user = userService.findByIdOrThrow(id);

        if (user.isAdmin()) {
             attr.addFlashAttribute("mensagemErro", "Não é possível bloquear outro administrador.");
        } else {
            user.setBlocked(true);
            userService.saveUser(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário bloqueado com sucesso!");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluirUsuario(@PathVariable UUID id, RedirectAttributes attr) {
        User user = userService.findByIdOrThrow(id);

        if (user.isAdmin()) {
            attr.addFlashAttribute("mensagemErro", "Não é possível excluir outro administrador.");
        } else {
            userService.delete(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        }
        return "redirect:/dashboard";
    }
    
    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable UUID id, RedirectAttributes attr) {
        User user = userService.findByIdOrThrow(id);
        if (user != null) {
            user.setBlocked(false);
            userService.saveUser(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário desbloqueado com sucesso!");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/usuarios/cadastrar")
    public String cadastrarUsuario(
            @Valid @ModelAttribute("novoUsuario") User novoUsuario, // -> Alterado para @ModelAttribute
            BindingResult result,
            RedirectAttributes attr,
            Model model,
            @AuthenticationPrincipal User admin) { // Usuário logado

        // Verifica se há erros de validação das anotações
        if (result.hasErrors()) {
            // Se houver erros, repopula o model e retorna para a view
            model.addAttribute("user", admin); // Adiciona o usuário logado de volta ao model
            
            // Repopula os dados da paginação para a view não quebrar
            int pageSize = 10;
            var categoriasPage = categoryService.findPaginated(0, pageSize);
            model.addAttribute("categorias", categoriasPage.getContent());
            model.addAttribute("categoriasTotalPages", categoriasPage.getTotalPages());
            model.addAttribute("categoriasCurrentPage", 0);

            var usuariosPage = userService.findPaginated(0, pageSize);
            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("usuariosTotalPages", usuariosPage.getTotalPages());
            model.addAttribute("usuariosCurrentPage", 0);
            
            // Adiciona um objeto de categoria vazio para o outro formulário na página não quebrar
             if (!model.containsAttribute("category")) {
                model.addAttribute("category", new Category());
            }

            return "user/dashboard";
        }
        

        // Lógica de negócio original
        if (userService.loginExists(novoUsuario.getLogin())) {
            attr.addFlashAttribute("mensagemErro", "Login já está em uso.");
            return "redirect:/dashboard";
        }

        userService.saveUser(novoUsuario); // O service deve cuidar da criptografia da senha

        attr.addFlashAttribute("mensagemSucesso", "Usuário cadastrado com sucesso!");
        return "redirect:/dashboard";
    }

    
    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuarioForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User admin, RedirectAttributes attr) {
        User usuario = userService.findByIdOrThrow(id);
        if (usuario.isAdmin() && !admin.isAdmin()) {
            attr.addFlashAttribute("mensagemErro", "Você não pode editar outro administrador.");
            return "redirect:/dashboard";
        }
        model.addAttribute("usuario", usuario);
        return "user/editar-user";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String salvarEdicaoUsuario(@PathVariable UUID id,
                                        @RequestParam String name,
                                        @RequestParam String login,
                                        @RequestParam String type,
                                        RedirectAttributes attr) {
        User user = userService.findByIdOrThrow(id);
        user.setName(name);
        user.setLogin(login);
        user.setType(type);
        userService.saveUser(user);
        attr.addFlashAttribute("mensagemSucesso", "Usuário atualizado com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}