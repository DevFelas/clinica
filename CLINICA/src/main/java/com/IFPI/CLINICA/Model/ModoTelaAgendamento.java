package com.IFPI.CLINICA.Model;

/**
 * Enumeração responsável por definir os estados de visualização da interface de agendamento.
 * Este componente auxilia na lógica de controle da interface gráfica (GUI),
 * determinando se os campos devem estar bloqueados para leitura ou abertos para modificação.
 */
public enum ModoTelaAgendamento {
    /**
     * Indica que a tela está em modo de apenas leitura, exibindo as informações detalhadas.
     */
    DETALHE,

    /**
     * Indica que a tela está em modo de alteração, permitindo a edição dos dados do registro.
     */
    EDICAO
}