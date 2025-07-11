package com.example.demo.controller; // Mantenha o seu pacote

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

// Remova imports não utilizados como AuthService, User, HttpSession, RedirectAttributes
// import com.example.demo.models.users.User;
// import com.example.demo.service.AuthService;
// import jakarta.servlet.http.HttpSession;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping()
    public ModelAndView getForm(ModelAndView mav) {
        mav.setViewName("auth/login");
        return mav;
    }

}