package com.example.demo.Models.enums;

public enum Categorys {
    SALARIO("Salário", Nature.ENTRADA),
    CASHBACK("Cashback", Nature.ENTRADA),
    RESGATE_INVESTIMENTO("Resgate Investimento", Nature.ENTRADA),
    OUTRAS_ENTRADAS("Outras Entradas", Nature.ENTRADA),

    SAUDE_REMEDIOS("Saúde e Remédios", Nature.SAIDA),
    ACADEMIA_PERSONAL("Academia e Personal", Nature.SAIDA),
    CARROS_UBER("Carros e Uber", Nature.SAIDA),
    EDUCACAO_CURSOS("Educação e Cursos", Nature.SAIDA),
    LAZER_TURISMO("Lazer e Turismo", Nature.SAIDA),
    CONDOMINIO("Condomínio", Nature.SAIDA),
    ENERGIA("Energia", Nature.SAIDA),
    CELULAR("Celular", Nature.SAIDA),
    INTERNET("Internet", Nature.SAIDA),
    ITENS_PESSOAIS("Itens Pessoais", Nature.SAIDA),
    FEIRA("Feira", Nature.SAIDA),
    CASA("Casa", Nature.SAIDA),
    IMPOSTOS("Impostos", Nature.SAIDA),
    OUTROS_GASTOS("Outros gastos", Nature.SAIDA),

    APORTE_RENDA_FIXA("Aporte Renda Fixa", Nature.INVESTIMENTO),
    APORTE_RENDA_VARIAVEL("Aporte Renda Variável", Nature.INVESTIMENTO),
    APORTE_RESERVA_EMERGENCIA("Aporte Reserva Emergencia", Nature.INVESTIMENTO),
    APORTE_PREVIDENCIA("Aporte Previdência", Nature.INVESTIMENTO);


    private final String descricao;
    private final Nature natureza;

    Categorys(String descricao, Nature natureza) {
        this.descricao = descricao;
        this.natureza = natureza;
    }

    public String getDescricao() {
        return descricao;
    }

    public Nature getNatureza() {
        return natureza;
    }
}
