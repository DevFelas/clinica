package com.IFPI.CLINICA.Model;

/**
 * Enumeração que define os estados possíveis de uma transação financeira no sistema.
 * Este componente é vital para o controle de auditoria e fluxo de caixa,
 * permitindo distinguir entre valores efetivados e previsões de recebimento ou pagamento.
 */
public enum StatusTransacao {
    /**
     * Indica que a transação foi devidamente liquidada, com a entrada ou saída
     * real de recursos do capital da clínica.
     */
    PAGO,

    /**
     * Representa uma transação registrada, mas ainda não efetivada financeiramente
     * (ex: contas a pagar ou a receber futuras).
     */
    PENDENTE,

    /**
     * Identifica transações que foram invalidadas por erro, desistência ou
     * retificação, sendo ignoradas em cálculos de lucro líquido.
     */
    CANCELADO
}