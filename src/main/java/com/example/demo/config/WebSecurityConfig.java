package com.example.demo.config;

// 1. Importe o seu novo handler e a anotação Autowired
import com.example.demo.config.security.CustomAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    // 2. Injete o seu handler customizado
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Suas regras de permissão continuam as mesmas
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/auth**"),
                    AntPathRequestMatcher.antMatcher("/css/**"),
                    AntPathRequestMatcher.antMatcher("/imagens/**"),
                    AntPathRequestMatcher.antMatcher("/error")
                ).permitAll()
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/correntistas/**")
                ).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // Configurações do formulário de login
            .formLogin(form -> form
                .loginPage("/auth")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/dashboard", true)
                
                // 👇 AQUI ESTÁ A ÚNICA MUDANÇA NECESSÁRIA 👇
                // Trocamos a URL de falha fixa por nosso handler inteligente
                .failureHandler(customAuthenticationFailureHandler)
                
                .permitAll()
            )
            // Configurações de logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/auth?logout")
                .permitAll()
            )
            // Desabilite o CSRF
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}