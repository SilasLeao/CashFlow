package com.example.demo.models.accounts;

import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.models.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "Conta")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "O número da conta é obrigatório.")
    @Size(max = 20, message = "O número da conta deve ter no máximo 20 caracteres.")
    private String number;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(min = 3, max = 50, message = "A descrição deve ter entre 3 e 50 caracteres.")
    private String description;

    @NotNull(message = "O tipo da conta é obrigatório.")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    private LocalDateTime finishDay;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;
}