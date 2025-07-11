package com.example.demo.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String redirectURL = "/auth?error=true"; // URL padrão para erros genéricos

        // Verifica se a exceção é do tipo 'LockedException' (usuário bloqueado)
        if (exception instanceof LockedException) {
            redirectURL = "/auth?blocked=true"; // URL específica para usuário bloqueado
        }

        // --- AQUI ESTÁ A MUDANÇA ---
        // 1. Definimos a URL de falha que queremos usar.
        setDefaultFailureUrl(redirectURL);
        
        // 2. Chamamos o método da classe pai (super) para que ele faça o redirecionamento
        // e qualquer outra tarefa interna do Spring (como salvar a exceção na sessão).
        super.onAuthenticationFailure(request, response, exception);
    }
}