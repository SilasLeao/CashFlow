package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.users.User;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping()
    public ModelAndView getForm(ModelAndView mav) {
        mav.setViewName("auth/login");
        mav.addObject("user", new User());
        return mav;
    }

    // @PostMapping
    // public ModelAndView login(User user, HttpSession session, ModelAndView mav, RedirectAttributes attr) {
    //     if (usuarioService.validar(username, password)) {
    //         session.setAttribute("usuario", username);
    //         return "redirect:/";
    //     } else {
    //         return "redirect:/login?error=true";
    //     }
    // }

    // @PostMapping("/auth")
    // public ModelAndView login(User user, HttpSession session, ModelAndView mav, RedirectAttributes attr) {
    //     if (usuarioService.validar(user.getLogin(), user.getPassword())) {
    //         session.setAttribute("usuario", user);
    //         mav.setViewName("redirect:/");
    //     } else {
    //         attr.addFlashAttribute("error", "Usuário ou senha inválidos.");
    //         mav.setViewName("redirect:/login");
    //     }
    //     return mav;
    // }

    @PostMapping
    public ModelAndView login(User user, HttpSession session, ModelAndView mav, RedirectAttributes attr) {
        User validUser = authService.isValid(user);

        if (validUser != null) {

            session.setAttribute("usuario", validUser);
            mav.setViewName("redirect:/");

        } else {

            attr.addFlashAttribute("mensagem", "Login e/ou senha inválidos!");
            mav.setViewName("redirect:/auth");

        }

        return mav;
    }

    // @PostMapping()
    // public ModelAndView login(@ModelAttribute("usuario") User usuario,
    //         HttpSession session,
    //         RedirectAttributes redirectAttributes) {
    //     User usuarioAutenticado = usuarioService.validar(usuario.getLogin(), usuario.getPassword());

    //     if (usuarioAutenticado != null) {
    //         session.setAttribute("usuario", usuarioAutenticado);
    //         return new ModelAndView("redirect:/home");
    //     } else {
    //         redirectAttributes.addFlashAttribute("erro", "Login ou senha inválidos.");
    //         return new ModelAndView("redirect:/usuario/login");
    //     }
    // }

    // @PostMapping
    // public ModelAndView valide(Correntista correntista, HttpSession session, ModelAndView model,
    //         RedirectAttributes redirectAttts) {
    //     if ((correntista = this.isValido(correntista)) != null) {
    //         session.setAttribute("usuario", correntista);
    //         model.setViewName("redirect:/home");
    //     } else {
    //         redirectAttts.addFlashAttribute("mensagem", "Login e/ou senha inválidos!");
    //         model.setViewName("redirect:/auth");
    //     }
    //     return model;
    // }
}
