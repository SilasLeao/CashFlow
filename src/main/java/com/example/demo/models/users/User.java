package com.example.demo.models.users;

import com.example.demo.models.accounts.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import org.springframework.security.core.userdetails.UserDetails; 

import java.util.Collection; 
import java.util.Collections; 
import java.util.List;
import java.util.UUID;

@Data // Gera getters, setters, toString, equals, hashCode
@AllArgsConstructor // Gera construtor com todos os campos
@NoArgsConstructor // Gera construtor sem argumentos
@Entity
@Table(name = "Users") // Mantenha o nome da sua tabela
public class User implements UserDetails { 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String password;
    private String login;
    private String type;     // "ADMIN" ou "NORMAL"

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE,
            CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Account> accounts;

    @Column(nullable = false)
    private boolean isBlocked = false; // Atributo para indicar se o usuário está bloqueado


    // --- Métodos da interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna a lista de papéis/roles do usuário.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.type.toUpperCase()));
    }

    @Override
    public String getUsername() {
        // Este método deve retornar o nome de usuário (login) do seu User.
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Retorna se a conta do usuário não expirou.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Retorna se a conta do usuário não está bloqueada.
        return !this.isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Retorna se as credenciais (senha) do usuário não expiraram.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Retorna se o usuário está habilitado.
        return !this.isBlocked;
    }
}