package com.IFPI.CLINICA.Util;

import com.IFPI.CLINICA.Model.Usuario;

public class SessaoUsuario {
    private static SessaoUsuario instance;
    private Usuario usuarioLogado;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void limparSessao() {
        this.usuarioLogado = null;
    }
}
