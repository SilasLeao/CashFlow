package com.example.demo.config;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 1. URLs que SÃO PERMITIDAS a TODOS (mesmo sem login)
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/auth**"), // Sua página de login (/auth e /auth?error, /auth?logout)
                    AntPathRequestMatcher.antMatcher("/css/**"), // Arquivos CSS
                    AntPathRequestMatcher.antMatcher("/imagens/**"), // Imagens
                    AntPathRequestMatcher.antMatcher("/error") // Página de erro padrão do Spring
                ).permitAll()
                // 2. URLs que exigem um PAPEL ESPECÍFICO (ex: ADMIN)
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/correntistas/**"),
                    AntPathRequestMatcher.antMatcher("/contas/**")
                ).hasRole("ADMIN")
                // 3. TODAS AS OUTRAS URLs exigem AUTENTICAÇÃO
                // Esta regra deve ser a última e mais abrangente para pegar todo o resto.
                .anyRequest().authenticated()
            )
            // Configurações do formulário de login
            .formLogin(form -> form
                .loginPage("/auth")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/dashboard", true) // Redireciona para /dashboard
                .failureUrl("/auth?error")
                .permitAll()
            )
            // Configurações de logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/auth?logout")
                .permitAll()
            )
            // Desabilite o CSRF por enquanto (lembre-se de habilitar para produção)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}