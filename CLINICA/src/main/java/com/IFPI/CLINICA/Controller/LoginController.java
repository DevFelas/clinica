package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Repository.UsuarioRepository;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

import java.io.IOException;

@Component
public class LoginController {

    @Autowired
    private ApplicationContext springContext;

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

        if (login.isEmpty() || senha.isEmpty()) {
            exibirAlerta("Campo Vazio", "Por favor, preencha todos os campos.");
            return;
        }

        // Busca no banco de dados
        Usuario usuario = repository.findByLoginAndSenha(login, senha);

        if (usuario != null) {
            SessaoUsuario.getInstance().setUsuarioLogado(usuario);
            System.out.println("Login realizado com sucesso! Bem-vindo: " + usuario.getLogin());

            // Próximo passo: Chamar o método para trocar de cena (Scene Builder)
            abrirTelaPrincipal();
        } else {
            exibirAlerta("Erro de Autenticação", "Usuário ou senha inválidos!");
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void abrirTelaPrincipal() {
        try {

            // 1. Carrega o FXML da tela principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/Agenda.fxml"));

            // 2. DIZ AO JAVAFX PARA USAR O SPRING PARA CRIAR O CONTROLLER
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            // 3. Pega a janela atual (Stage) a partir de qualquer componente da tela de login
            Stage stage = (Stage) campoLogin.getScene().getWindow();

            // 4. Define a nova cena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true); // Garante que a principal também abra grande
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível carregar a tela principal.");
        }
    }

}
