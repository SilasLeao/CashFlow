package com.example.demo.models.users;

import com.example.demo.models.accounts.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String password;

    private String login;

    private String type;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE,
            CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Account> accounts;

    @Column(nullable = false)
    private boolean isBlocked = false;

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

}
