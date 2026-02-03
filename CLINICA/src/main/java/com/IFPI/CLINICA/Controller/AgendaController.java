package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Util.SessaoUsuario;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

import java.awt.*;


@Component
public class AgendaController {

    @FXML
    private Label labelBemVindo;

    @FXML
    public void initialize() {
        // Recupera o usuário da sessão que você criou!
        if (SessaoUsuario.getInstance().getUsuarioLogado() != null) {
            String nome = SessaoUsuario.getInstance().getUsuarioLogado().getLogin();
            labelBemVindo.setText("Bem-vindo, " + nome + "!");
        }
    }

    @FXML
    private void handleSair() {
        SessaoUsuario.getInstance().limparSessao();
        // Lógica para voltar para o login...
    }

}
