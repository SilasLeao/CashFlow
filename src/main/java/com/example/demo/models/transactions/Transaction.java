package com.example.demo.models.transactions;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.enums.MovementType;

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

    private Date date;

    @Enumerated(EnumType.STRING)
    private MovementType movement;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    @ManyToOne
    private Category category;


}
