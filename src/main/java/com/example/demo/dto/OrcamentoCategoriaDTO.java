package com.example.demo.dto;

import com.example.demo.models.enums.Nature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrcamentoCategoriaDTO {
    private String name;
    private Nature  natureza;
    private Map<String, Double> valores;
    private Double total;
}