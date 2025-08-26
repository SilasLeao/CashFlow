package com.example.demo.models.transactions;

import com.example.demo.models.enums.Nature;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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

    @NotBlank(message = "O nome da categoria é obrigatório.")
    private String name;

    @NotNull(message = "A natureza da categoria é obrigatória.")
    @Enumerated(EnumType.STRING)
    private Nature nature;

    @NotNull(message = "O status (ativo/inativo) é obrigatório.")
    private Boolean active;

    @NotNull(message = "O índice de ordem é obrigatório.")
    @Min(value = 0, message = "O índice de ordem não pode ser negativo.")
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