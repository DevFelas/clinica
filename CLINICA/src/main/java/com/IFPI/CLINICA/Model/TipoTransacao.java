package com.IFPI.CLINICA.Model;

/**
 * Enumeração que define a natureza contábil de uma movimentação financeira.
 * Este componente estabelece a distinção fundamental entre o aporte de recursos
 * e a retirada de capital, orientando os cálculos de balanço patrimonial da clínica.
 */
public enum TipoTransacao {
    /**
     * Representa o aporte de capital ou receitas provenientes de atendimentos
     * e outras fontes de faturamento.
     */
    ENTRADA,

    /**
     * Representa a retirada de capital para pagamento de despesas operacionais,
     * salários, insumos ou outros custos.
     */
    SAIDA
}