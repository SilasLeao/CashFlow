package com.example.demo.Models.Transactions;


import com.example.demo.Models.enums.Categorys;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Table(name = "Category")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Categorys categorys;

    //private String name;
    //private String nature;
    private Boolean isTrue;

    @OneToMany
    private List<Transaction> transactions;

    private Integer order;


}
