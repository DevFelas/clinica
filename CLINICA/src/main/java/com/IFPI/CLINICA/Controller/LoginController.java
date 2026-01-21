package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Repository.UsuarioRepository;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

@Component
public class LoginController {

    @FXML
    private TextField campoLogin;

    @FXML
    private PasswordField campoSenha;

    @Autowired
    private UsuarioRepository repository; // O Spring injeta a conexão com o banco aqui

    @FXML
    private void handleLogin() {
        String login = campoLogin.getText();
        String senha = campoSenha.getText();

        Usuario usuario = repository.findByLoginAndSenha(login, senha);

        if (usuario != null) {
            SessaoUsuario.getInstance().setUsuarioLogado(usuario);

            //FuncaoQueLevaParaTelaPrincipal();
        } else {
            //exibirAlerta("Erro", "Usuário ou senha inválidos!");
        }
    }

}
