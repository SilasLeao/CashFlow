package com.example.demo.Models.Transactions;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import com.example.demo.Models.Accounts.Account;
import com.example.demo.Models.enums.MovementType;

@Table(name = "Transaction")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String description;

    private Double value;

    @Enumerated(EnumType.STRING)
    private MovementType movement;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToMany
    @JoinTable(
        name = "transacao_conta",
        joinColumns = @JoinColumn(name = "transacao_id"),
        inverseJoinColumns = @JoinColumn(name = "conta_id")
    )
    private List<Account> accounts;

    @ManyToOne
    private Category category;


}
