package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Perfil;
import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Service.FxmlLoaderService;
import com.IFPI.CLINICA.Util.Navigator;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;


import javafx.event.ActionEvent;

public abstract class SuperController {

    @Autowired protected Navigator navigator;
    @Autowired protected FxmlLoaderService fxmlLoaderService;

    protected void aplicarPermissoesUI(javafx.scene.control.Button btnFinanceiro,
                                       javafx.scene.control.Label textUsuario) {

        Usuario usuario = SessaoUsuario.getInstance().getUsuarioLogado();

        if (usuario == null || usuario.getPerfil() == null) {
            textUsuario.setText("SEM USUÁRIO");
            btnFinanceiro.setVisible(false);
            return;
        }

        if (usuario.getPerfil() == Perfil.RECEPCIONISTA) {
            btnFinanceiro.setVisible(false);
            textUsuario.setText("RECEPCIONISTA");
        } else if (usuario.getPerfil() == Perfil.ADMIN) {
            btnFinanceiro.setVisible(true);
            textUsuario.setText("ADMINISTRADOR");
        } else {
            // opcional: outros perfis
            btnFinanceiro.setVisible(false);
            textUsuario.setText(usuario.getPerfil().name());
        }
    }

    @FXML
    protected void irPara(ActionEvent event) {
        Node origem = (Node) event.getSource();

        Object ud = origem.getUserData();
        if (ud == null) {
            throw new IllegalArgumentException("Botão sem userData com o caminho do FXML.");
        }

        navigator.trocarPagina(origem, ud.toString());
    }

    @FXML
    public void sair(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Sair do sistema");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente sair do sistema?");

        confirm.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {

                // limpa sessão
                SessaoUsuario.getInstance().limparSessao();

                // volta para login
                navigator.trocarPagina(
                        (Node) event.getSource(),
                        "/view/pages/Login.fxml"
                );
            }
        });
    }

}
