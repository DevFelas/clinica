package com.IFPI.CLINICA.Util;

import com.IFPI.CLINICA.Model.Paciente;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe utilitária para compartilhamento temporário de dados entre telas/controllers.
 *
 * <p>Funciona como um "cache" simples em memória, armazenando um {@link Paciente}
 * que será utilizado em fluxos como edição (ex.: selecionar um paciente em uma tela
 * e abrir outra tela para editar).</p>
 *
 * <p><b>Atenção:</b> como o atributo é {@code static}, o valor é compartilhado globalmente
 * na aplicação e pode causar efeitos colaterais se não for limpo no momento certo.
 * Sempre chame {@link #limpar()} após concluir o uso.</p>
 */
public class DataShare {

    /**
     * Paciente selecionado para edição.
     *
     * <p>É {@code static} para permitir acesso direto por diferentes controllers,
     * sem necessidade de injeção de dependência.</p>
     */
    @Setter
    @Getter
    private static Paciente pacienteParaEditar;

    /**
     * Limpa os dados compartilhados, removendo a referência do paciente em edição.
     *
     * <p>Recomendado chamar após concluir o fluxo de edição, para evitar reutilização
     * indevida do mesmo objeto em telas futuras.</p>
     */
    public static void limpar() {
        pacienteParaEditar = null;
    }
}