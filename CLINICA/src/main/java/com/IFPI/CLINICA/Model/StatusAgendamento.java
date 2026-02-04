package com.IFPI.CLINICA.Model;

/**
 * Enumeração que define o ciclo de vida e a situação atual de um agendamento.
 * Este componente é essencial para o controle de fluxo da clínica, permitindo
 * o rastreamento desde a marcação inicial até a conclusão ou desistência do serviço.
 */
public enum StatusAgendamento {
    /**
     * Indica que o agendamento foi registrado no sistema, mas o atendimento
     * ainda não ocorreu.
     */
    AGENDADA,

    /**
     * Confirma que o procedimento foi executado com sucesso e o fluxo
     * de atendimento foi concluído.
     */
    REALIZADA,

    /**
     * Registra que o agendamento foi desmarcado por iniciativa do paciente
     * ou da clínica, invalidando o horário reservado.
     */
    CANCELADA
}