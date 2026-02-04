package com.IFPI.CLINICA.Util;

import com.IFPI.CLINICA.Model.Usuario;

/**
 * Gerencia a sessão do usuário na aplicação, armazenando o usuário atualmente logado.
 *
 * <p>Implementa o padrão Singleton para garantir uma única instância acessível
 * globalmente durante a execução. É útil para compartilhar informações do usuário
 * entre telas/controllers sem precisar passar parâmetros manualmente.</p>
 *
 * <p><b>Observação:</b> esta implementação não é thread-safe. Em aplicações JavaFX
 * isso normalmente não é um problema (por operar majoritariamente na UI thread),
 * mas se você acessar a sessão a partir de múltiplas threads, considere sincronização.</p>
 */
public class SessaoUsuario {
    /**
     * Instância única (Singleton) da sessão.
     */
    private static SessaoUsuario instance;
    /**
     * Usuário atualmente autenticado/logado no sistema.
     */
    private Usuario usuarioLogado;

    /**
     * Construtor privado para impedir instanciação externa (padrão Singleton).
     */
    private SessaoUsuario() {}

    /**
     * Retorna a instância única da sessão do usuário, criando-a na primeira chamada.
     *
     * @return instância singleton de {@link SessaoUsuario}
     */
    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    /**
     * Define o usuário que ficará armazenado como "logado" na sessão.
     *
     * @param usuario usuário autenticado que será mantido na sessão
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    /**
     * Obtém o usuário atualmente logado.
     *
     * @return usuário logado; ou {@code null} se não houver sessão ativa
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Limpa a sessão atual, removendo o usuário logado.
     *
     * <p>Usado normalmente em logout, expiração de sessão ou ao retornar para a tela de login.</p>
     */
    public void limparSessao() {
        this.usuarioLogado = null;
    }
}
