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
@RequestMapping("/")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping()
    public ModelAndView getForm(ModelAndView mav) {
        mav.setViewName("auth/login");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping
    public ModelAndView login(User user, HttpSession session, ModelAndView mav, RedirectAttributes attr) {
        User validUser = authService.isValid(user);

        if (validUser != null) {

            session.setAttribute("usuario", validUser);
            mav.setViewName("redirect:/dashboard");

        } else {

            attr.addFlashAttribute("mensagem", "Login e/ou senha inválidos!");
            mav.setViewName("redirect:/");

        }

        return mav;
    }

}
