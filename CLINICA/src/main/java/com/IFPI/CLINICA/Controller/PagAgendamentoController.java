package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class PagAgendamentoController implements Initializable {

    @Autowired
    private Navigator navigator;

    @FXML
    private TextField cpfField;

    @FXML
    private ComboBox<String> procedimentoCombo;

    @FXML
    private ComboBox<String> horarioCombo;

    @FXML
    private DatePicker dataPicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ADICIONE TODOS OS 8 PROCEDIMENTOS DO FINANCEIRO
        procedimentoCombo.getItems().addAll(
                "Consulta",
                "Limpeza",
                "Exodontia",
                "Prótese",
                "Implante",
                "Manutenção de Aparelho",
                "Montagem de aparelho",
                "Restauração"
        );

        // Horários disponíveis (mantive os originais, mas pode expandir se quiser)
        horarioCombo.getItems().addAll(
                "08:00",
                "09:00",
                "10:00"
        );
    }

    // PAGINAÇÃO DO MENU LATERAL

    // Botão para ir para tela da Agenda
    @FXML
    private void irParaAgenda(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Agenda.fxml"
        );
    }

    // Botão para ir para tela de Pacintes
    @FXML
    private void irParaPacientes(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/TodosPacientes.fxml"
        );
    }

    // Botão para ir para tela de Registro (Descomentar quando a tela existir)
    @FXML
    private void irParaRegistro(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Registro.fxml"
        );
    }

    // Botão para ir para tela Financeiro (Descomentar quando a tela existir
    @FXML
    private void irParaFinanceiro(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Financeiro.fxml"
        );
    }

    @FXML
    private void onAgendar() {
        if (!validarFormulario()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Agendamento");
        alert.setHeaderText(null);
        alert.setContentText("Agendamento validado com sucesso.");
        alert.showAndWait();
    }

    private boolean validarFormulario() {
        String cpf = cpfField.getText();

        if (cpf == null || cpf.isBlank()) {
            mostrarAlerta("Informe o CPF do paciente.");
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (!cpf.matches("\\d{11}")) {
            mostrarAlerta("CPF inválido. Deve conter 11 números.");
            return false;
        }

        if (procedimentoCombo.getValue() == null) {
            mostrarAlerta("Selecione um procedimento.");
            return false;
        }

        if (dataPicker.getValue() == null) {
            mostrarAlerta("Selecione uma data.");
            return false;
        }

        if (horarioCombo.getValue() == null) {
            mostrarAlerta("Selecione um horário.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}