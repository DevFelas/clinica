package com.IFPI.CLINICA.Model;

public enum CategoriaTransacao {
    CONSULTA("Consulta"),
    LIMPEZA("Limpeza"),
    EXODONTIA("Exodontia"),
    PROTESE("Prótese"),
    IMPLANTE("Implante"),
    MANUTENCAO("Manutenção"),
    MONTAGEM("Montagem"),
    RESTAURACAO("Restauração"),
    INSUMOS("Insumos Clínicos"),
    ALUGUEL("Aluguel"),
    SALARIO("Salário"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}