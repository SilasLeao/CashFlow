package com.example.demo.models.users;

import com.example.demo.models.accounts.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority; // Importar
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importar
import org.springframework.security.core.userdetails.UserDetails; // Importar

import java.util.Collection; // Importar
import java.util.Collections; // Importar
import java.util.List;
import java.util.UUID;

@Data // Gera getters, setters, toString, equals, hashCode
@AllArgsConstructor // Gera construtor com todos os campos
@NoArgsConstructor // Gera construtor sem argumentos
@Entity
@Table(name = "Users") // Mantenha o nome da sua tabela
public class User implements UserDetails { // <<< Adicione 'implements UserDetails'
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String password;
    private String login; // Este será o 'username' para o Spring Security
    private String type;     // "ADMIN" ou "NORMAL"

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE,
            CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Account> accounts;

    @Column(nullable = false)
    private boolean isBlocked = false; // Atributo para indicar se o usuário está bloqueado

    // Os getters e setters para 'id', 'name', 'password', 'login', 'type', 'accounts'
    // e 'isBlocked' são gerados automaticamente pelo @Data do Lombok.
    // Você já tem o isBlocked() e setBlocked() explícitos, pode mantê-los se preferir,
    // mas o @Data já os geraria.


    // --- Métodos da interface UserDetails que você PRECISA IMPLEMENTAR ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Isso retorna a lista de papéis/roles do usuário.
        // Baseado no seu atributo 'type' ("ADMIN" ou "NORMAL").
        // Convertemos para maiúsculas e adicionamos o prefixo "ROLE_".
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
        // Por padrão, geralmente é 'true', a menos que você implemente lógica de expiração de conta.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Retorna se a conta do usuário não está bloqueada.
        // Usaremos seu atributo 'isBlocked' para isso.
        // Se 'isBlocked' for true, a conta está bloqueada, então retorna false.
        return !this.isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Retorna se as credenciais (senha) do usuário não expiraram.
        // Por padrão, geralmente é 'true', a menos que você implemente lógica de expiração de senha.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Retorna se o usuário está habilitado.
        // Usaremos seu atributo 'isBlocked' para indicar se o usuário está ativo.
        // Se 'isBlocked' for true, o usuário não está habilitado, então retorna false.
        return !this.isBlocked;
    }
}