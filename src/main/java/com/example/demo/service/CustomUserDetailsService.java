package com.example.demo.service; // Mantenha seu pacote

import com.example.demo.models.users.User; // Seu modelo de usuário
import com.example.demo.repo.UserRepository; // Seu repositório de usuários
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// REMOVA ESTAS IMPORTAÇÕES, pois seu User agora as gerencia:
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import java.util.Collections;
// import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Busca o usuário pelo campo 'login' (que é o 'username' para o Spring Security)
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + login));

        // --- A MUDANÇA FINAL AQUI ---
        // Como seu modelo 'User' agora IMPLEMENTA 'UserDetails',
        // você pode simplesmente retornar o próprio objeto 'user'.
        // Ele já tem a lógica para fornecer o username, password e as authorities.
        return user;
        // --- FIM DA MUDANÇA ---
    }
}