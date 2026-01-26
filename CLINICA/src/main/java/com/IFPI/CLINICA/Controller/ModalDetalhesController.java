package com.IFPI.CLINICA.Controller;

import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

@Component
public class ModalDetalhesController {

    @FXML private TextField txtNome;
    @FXML private TextField txtContato;
    @FXML private TextField txtCpf;
    @FXML private TextField txtHorario;
    @FXML private TextField txtProcedimento;
    @FXML private TextField txtValor;

    @FXML
    public void initialize() {
        System.out.println("Modal de detalhes carregada com sucesso!");
    }

    @FXML
    private void fecharModal(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}