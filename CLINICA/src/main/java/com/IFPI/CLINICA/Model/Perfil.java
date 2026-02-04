package com.IFPI.CLINICA.Model;

/**
 * Enumeração que define os perfis de acesso disponíveis no sistema.
 * Este componente é a base para a implementação do controle de permissões,
 * garantindo que as funcionalidades sejam segmentadas de acordo com as
 * atribuições de cada colaborador na clínica.
 */
public enum Perfil {
    /**
     * Representa o perfil de administrador, com acesso total às configurações,
     * relatórios financeiros e gestão do sistema.
     */
    ADMIN,

    /**
     * Representa o perfil de recepção, focado em agendamentos, cadastros
     * de pacientes e rotinas operacionais cotidianas.
     */
    RECEPCIONISTA
}
