package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Service.clinicaTeste.FinanceiroService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

@Component
public class NovaDespesaDialogController implements Initializable {

    @Autowired
    private FinanceiroService financeiroService;

    @FXML private DatePicker dataField;
    @FXML private TextField descricaoField;
    @FXML private ComboBox<CategoriaTransacao> categoriaCombo;
    @FXML private TextField valorField;
    @FXML private ComboBox<StatusTransacao> statusCombo;

    private final ObservableList<CategoriaTransacao> categorias =
            FXCollections.observableArrayList(CategoriaTransacao.values());
    private final ObservableList<StatusTransacao> statusList =
            FXCollections.observableArrayList(StatusTransacao.values());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dataField.setValue(LocalDate.now());

        categoriaCombo.setItems(categorias);
        categoriaCombo.setValue(CategoriaTransacao.INSUMOS);

        statusCombo.setItems(statusList);
        statusCombo.setValue(StatusTransacao.PENDENTE);

        configurarMascaraValor();

        categoriaCombo.setOnAction(event -> {
            if (categoriaCombo.getValue() != null) {
                descricaoField.setText("Despesa - " + categoriaCombo.getValue().getDescricao());
            }
        });
    }

    private void configurarMascaraValor() {
        valorField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                valorField.setText(oldVal);
            }
        });
    }

    @FXML
    private void salvar() {
        if (!validarFormulario()) return;

        try {
            TransacaoFinanceira transacao = new TransacaoFinanceira();
            transacao.setData(dataField.getValue());
            transacao.setDataCadastro(LocalDateTime.now());
            transacao.setDescricao(descricaoField.getText());
            transacao.setCategoria(categoriaCombo.getValue());
            transacao.setValor(new BigDecimal(valorField.getText().replace(",", ".")));
            transacao.setStatus(statusCombo.getValue());
            transacao.setTipo(TipoTransacao.SAIDA);

            financeiroService.criarTransacao(transacao);

            alertSucesso("Despesa cadastrada com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            alertErro("Erro ao salvar despesa", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        ((Stage) dataField.getScene().getWindow()).close();
    }

    private boolean validarFormulario() {
        StringBuilder erros = new StringBuilder();

        if (dataField.getValue() == null) erros.append("• Data é obrigatória\n");
        if (descricaoField.getText().isBlank()) erros.append("• Descrição é obrigatória\n");
        if (categoriaCombo.getValue() == null) erros.append("• Categoria é obrigatória\n");
        if (valorField.getText().isBlank()) erros.append("• Valor é obrigatório\n");
        if (statusCombo.getValue() == null) erros.append("• Status é obrigatório\n");

        if (!erros.isEmpty()) {
            alertAviso("Validação", erros.toString());
            return false;
        }
        return true;
    }

    private void alertSucesso(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertErro(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(titulo);
        a.showAndWait();
    }

    private void alertAviso(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.setTitle(titulo);
        a.showAndWait();
    }
}
