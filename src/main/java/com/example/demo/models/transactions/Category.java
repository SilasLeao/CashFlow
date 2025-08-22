package com.example.demo.models.transactions;


import com.example.demo.models.enums.Nature;

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

    private String name;

    @Enumerated(EnumType.STRING)
    private Nature nature;

    private Boolean active;

    private Integer orderIndex;

    @Transient
    private boolean editing = false;

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }


    @OneToMany(mappedBy = "category", cascade = {CascadeType.REMOVE,
            CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Transaction> transactions;


    public Nature getNatureza() {
        return this.nature;
    }

    public Integer getOrdem() {
        return this.orderIndex != null ? this.orderIndex : 0;
    }
}