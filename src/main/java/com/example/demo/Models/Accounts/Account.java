package com.example.demo.Models.Accounts;


import com.example.demo.Models.Transactions.Transaction;
import com.example.demo.Models.Users.User;
import com.example.demo.Models.enums.AccountType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    private User user;

    private String number;

    private String description;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    private Date finishDay;

    @ManyToMany(mappedBy = "accounts")
    private List<Transaction> transactions;




}
