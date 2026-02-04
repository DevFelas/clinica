package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Repository.UsuarioRepository;
import com.IFPI.CLINICA.Util.Navigator;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Controlador responsável pela lógica de autenticação da tela de Login.
 * Gerencia a entrada de dados do usuário, validação de credenciais
 * e a navegação para a tela principal do sistema.
 */
@Component
public class LoginController {

    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Button btnEntrar;

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private Navigator navigator;

    /**
     * Processa a tentativa de login do usuário.
     * Valida se os campos estão preenchidos, consulta o repositório por credenciais
     * válidas e, em caso de sucesso, inicia a sessão e navega para a agenda.
     */
    @FXML
    private void handleLogin() {
        String login = campoLogin.getText();
        String senha = campoSenha.getText();

        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Campos obrigatórios",
                    "Por favor, preencha login e senha."
            );
            return;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByLoginAndSenha(login, senha);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Guarda o usuário logado na sessão
            SessaoUsuario.getInstance().setUsuarioLogado(usuario);

            System.out.println("Login realizado com sucesso. Usuário: " + usuario.getLogin());
            irParaAgenda(btnEntrar);
        } else {
            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro de autenticação",
                    "Usuário ou senha inválidos."
            );
        }
    }

    /**
     * Realiza a transição de tela para a interface da Agenda.
     * @param origem O componente visual que disparou a ação (usado para localizar a Stage).
     */
    private void irParaAgenda(Node origem) { navigator.trocarPagina(origem, "/view/pages/Agenda.fxml"); }

    /**
     * Utilitário para exibição de mensagens de alerta ao usuário via JavaFX Alert.
     * * @param tipo O tipo do alerta (Informativo, Erro, Aviso, etc).
     * @param titulo O título da janela de alerta.
     * @param mensagem O texto descritivo do erro ou aviso.
     */
    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
