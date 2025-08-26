package com.example.demo.controller;

import com.example.demo.models.enums.Nature;
import com.example.demo.models.transactions.Category;
import com.example.demo.models.users.User;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

// -> REFATORADO: Regra de segurança aplicada a todos os métodos da classe.
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired private ExtratoService extratoService;
    @Autowired private CategoryService categoryService;
    @Autowired private OrcamentoService orcamentoService;
    @Autowired private UserService userService;

    // -> REFATORADO: Sobrescreve a regra da classe para permitir qualquer usuário autenticado.
    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String showDashboard(Model model,
                                @AuthenticationPrincipal User user,
                                @RequestParam(defaultValue = "0") int pageCategories,
                                @RequestParam(defaultValue = "0") int pageUsers) {
        
        // LÓGICA ORIGINAL MANTIDA EXATAMENTE IGUAL
        if ("NORMAL".equals(user.getType())) {
            return "redirect:/contas";
        }

        model.addAttribute("user", user);

        // LÓGICA ORIGINAL MANTIDA EXATAMENTE IGUAL
        if ("ADMIN".equals(user.getType())) {
            int pageSize = 10;
            var categoriasPage = categoryService.findPaginated(pageCategories, pageSize);
            model.addAttribute("categorias", categoriasPage.getContent());
            model.addAttribute("categoriasTotalPages", categoriasPage.getTotalPages());
            model.addAttribute("categoriasCurrentPage", pageCategories);

            var usuariosPage = userService.findPaginated(pageUsers, pageSize);
            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("usuariosTotalPages", usuariosPage.getTotalPages());
            model.addAttribute("usuariosCurrentPage", pageUsers);
        }

        return "user/dashboard";
    }

    // -> REFATORADO: Verificação de permissão manual removida. Lógica interna mantida.
    @PostMapping("/dashboard/criarCategoria")
    public String criarCategoria(@RequestParam String name,
                                 @RequestParam String nature,
                                 @RequestParam Integer orderIndex,
                                 @RequestParam Boolean active,
                                 RedirectAttributes attr,
                                 @AuthenticationPrincipal User user) { // O parâmetro 'user' não é mais necessário aqui, mas foi mantido para não quebrar a assinatura caso esteja sendo usado em algum outro lugar (ex: logs).
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

    @GetMapping("/dashboard/editarCategoria/{id}")
    public String editarCategoriaForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user) {
        // LÓGICA ORIGINAL MANTIDA
        model.addAttribute("user", user);
        Category categoria = categoryService.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        List<Category> categorias = categoryService.findAll();
        categorias.forEach(cat -> cat.setEditing(cat.getId().equals(id)));
        model.addAttribute("categorias", categorias);
        return "user/dashboard";
    }

    // -> REFATORADO: Verificação de permissão manual removida. Lógica interna mantida.
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

    // -> REFATORADO: Verificação de permissão manual removida. Lógica interna mantida.
    @PostMapping("/usuarios/{id}/bloquear")
    public String bloquearUsuario(@PathVariable UUID id, RedirectAttributes attr, @AuthenticationPrincipal User admin) {
        User user = userService.findByIdOrThrow(id);

        // LÓGICA ORIGINAL MANTIDA EXATAMENTE IGUAL
        if (user != null && !"ADMIN".equals(user.getType())) {
            user.setBlocked(true);
            userService.saveUser(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário bloqueado com sucesso!");
        } else if (user != null && "ADMIN".equals(user.getType())) {
            attr.addFlashAttribute("mensagemErro", "Não é possível bloquear outro administrador.");
        }
        return "redirect:/dashboard";
    }

    // -> REFATORADO: Verificação de permissão manual removida. Lógica interna mantida.
    @PostMapping("/usuarios/{id}/excluir")
    public String excluirUsuario(@PathVariable UUID id, RedirectAttributes attr, @AuthenticationPrincipal User admin) {
        User user = userService.findByIdOrThrow(id);

        // LÓGICA ORIGINAL MANTIDA EXATAMENTE IGUAL
        if (user != null && !"ADMIN".equals(user.getType())) {
            userService.delete(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        } else if (user != null && "ADMIN".equals(user.getType())) {
            attr.addFlashAttribute("mensagemErro", "Não é possível excluir outro administrador.");
        }
        return "redirect:/dashboard";
    }
    
    // ... E assim por diante para todos os outros métodos.
    // O padrão é o mesmo: remover o IF de autorização inicial e manter o resto do código do método idêntico.
    
    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable UUID id, RedirectAttributes attr, @AuthenticationPrincipal User admin) {
        User user = userService.findByIdOrThrow(id);
        if (user != null) {
            user.setBlocked(false);
            userService.saveUser(user);
            attr.addFlashAttribute("mensagemSucesso", "Usuário desbloqueado com sucesso!");
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
        if (!type.equals("NORMAL") && !type.equals("ADMIN")) {
            attr.addFlashAttribute("mensagemErro", "Tipo de usuário inválido.");
            return "redirect:/dashboard";
        }
        if (userService.loginExists(login)) {
            attr.addFlashAttribute("mensagemErro", "Login já está em uso.");
            return "redirect:/dashboard";
        }
        User novoUsuario = new User();
        novoUsuario.setName(name);
        novoUsuario.setLogin(login);
        novoUsuario.setPassword(password);
        novoUsuario.setType(type);
        novoUsuario.setBlocked(false);
        userService.saveUser(novoUsuario);
        attr.addFlashAttribute("mensagemSucesso", "Usuário cadastrado com sucesso!");
        return "redirect:/dashboard";
    }
    
    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuarioForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User admin, RedirectAttributes attr) {
        User usuario = userService.findByIdOrThrow(id);
        if ("ADMIN".equals(usuario.getType()) && !"ADMIN".equals(admin.getType())) {
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
                                        @AuthenticationPrincipal User admin,
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
    @PreAuthorize("isAuthenticated()") // Qualquer usuário autenticado pode acessar a raiz para ser redirecionado
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}