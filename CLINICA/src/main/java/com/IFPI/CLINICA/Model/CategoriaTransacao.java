package com.IFPI.CLINICA.Model;

/**
 * Enumeração que define as categorias de classificação para transações financeiras.
 * Este componente é fundamental para a organização contábil do sistema,
 * permitindo o agrupamento de receitas e despesas por tipos pré-definidos.
 */
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

    /**
     * Atributo que armazena a representação textual amigável da categoria.
     */
    private final String descricao;

    /**
     * Construtor privado que associa uma descrição a cada constante da enumeração.
     * @param descricao Texto descritivo da categoria.
     */
    CategoriaTransacao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Recupera a descrição associada à constante.
     * @return String contendo o nome formatado da categoria.
     */
    public String getDescricao() {
        return descricao;
    }
}