package com.IFPI.CLINICA.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public class PagCadasPessoaController implements Initializable {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtCpf;

    @FXML
    private DatePicker dpNascimento;

    @FXML
    private TextField txtContato;

    @FXML
    private TextField txtRua;

    @FXML
    private TextField txtBairro;

    @FXML
    private TextField txtCidade;

    @FXML
    private TextField txtNumero;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    @FXML
    private void cadastrarPaciente() {

        // 1. Ler dados da tela
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        LocalDate nascimento = dpNascimento.getValue();
        String contato = txtContato.getText();

        String rua = txtRua.getText();
        String bairro = txtBairro.getText();
        String cidade = txtCidade.getText();
        String numero = txtNumero.getText();

        // 2. Validação básica
        if (nome == null || nome.isBlank()) {
            mostrarErro("Nome é obrigatório.");
            return;
        }

        if (cpf == null || cpf.isBlank()) {
            mostrarErro("CPF é obrigatório.");
            return;
        }

        if (nascimento == null) {
            mostrarErro("Data de nascimento é obrigatória.");
            return;
        }

        // 3. Criar objeto Paciente (exemplo)
        Paciente paciente = new Paciente(
                nome,
                cpf,
                nascimento,
                contato,
                rua,
                bairro,
                cidade,
                numero
        );

        // 4. Por enquanto, só imprime no console
        System.out.println("Paciente cadastrado:");
        System.out.println(paciente);

        // 5. Limpar formulário
        limparCampos();

        // 6. Mensagem de sucesso
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Paciente cadastrado com sucesso!");
        alert.showAndWait();
    }

    // ===== MÉTODOS AUXILIARES =====

    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        dpNascimento.setValue(null);
        txtContato.clear();

        txtRua.clear();
        txtBairro.clear();
        txtCidade.clear();
        txtNumero.clear();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
