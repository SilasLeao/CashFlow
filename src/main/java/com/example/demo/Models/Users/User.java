package com.example.demo.Models.Users;

import com.example.demo.Models.Accounts.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private PasswordEncoder password;

    private String login;

    private String type;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE,
            CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Account> accounts;

}
