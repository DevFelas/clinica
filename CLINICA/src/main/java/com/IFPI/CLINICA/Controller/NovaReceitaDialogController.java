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
public class NovaReceitaDialogController implements Initializable {

    @Autowired
    private FinanceiroService financeiroService;

    @FXML private DatePicker dataField;
    @FXML private TextField descricaoField;
    @FXML private TextField valorField;
    @FXML private ComboBox<CategoriaTransacao> categoriaCombo;
    @FXML private ComboBox<StatusTransacao> statusCombo;
    @FXML private ComboBox<String> tipoReceitaCombo;

    private final ObservableList<CategoriaTransacao> categorias =
            FXCollections.observableArrayList(CategoriaTransacao.values());

    private final ObservableList<StatusTransacao> statusList =
            FXCollections.observableArrayList(StatusTransacao.values());

    private final ObservableList<String> tiposReceita =
            FXCollections.observableArrayList(
                    "Consulta", "Limpeza", "Exodontia", "Prótese",
                    "Implante", "Manutenção", "Montagem", "Restauração", "Outros"
            );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("=== NOVA RECEITA DIALOG INITIALIZE ===");

        dataField.setValue(LocalDate.now());

        categoriaCombo.setItems(categorias);
        categoriaCombo.setValue(CategoriaTransacao.CONSULTA);

        statusCombo.setItems(statusList);
        statusCombo.setValue(StatusTransacao.PAGO);

        tipoReceitaCombo.setItems(tiposReceita);
        tipoReceitaCombo.setValue("Consulta");

        configurarMascaraValor();

        tipoReceitaCombo.setOnAction(e -> {
            if (!"Outros".equals(tipoReceitaCombo.getValue())) {
                descricaoField.setText(tipoReceitaCombo.getValue());
            }
        });

        System.out.println("Diálogo de nova receita configurado");
    }

    private void configurarMascaraValor() {
        valorField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*([.,]\\d{0,2})?")) {
                valorField.setText(oldVal);
            }
        });
    }

    @FXML
    private void salvar() {
        System.out.println("=== SALVANDO NOVA RECEITA ===");
        if (!validarFormulario()) return;

        try {
            // Criar transação usando o builder
            TransacaoFinanceira transacao = TransacaoFinanceira.builder()
                    .data(dataField.getValue())
                    .dataCadastro(LocalDateTime.now())
                    .descricao(descricaoField.getText())
                    .categoria(categoriaCombo.getValue())
                    .tipo(TipoTransacao.ENTRADA)
                    .status(statusCombo.getValue())
                    .build();

            // Converter o valor corretamente
            String valorTexto = valorField.getText().replace(",", ".");
            if (valorTexto.isEmpty()) {
                valorTexto = "0.00";
            }
            transacao.setValor(new BigDecimal(valorTexto));

            // Adicione um log para verificar
            System.out.println("Salvando receita:");
            System.out.println("  - Descrição: " + transacao.getDescricao());
            System.out.println("  - Valor: " + transacao.getValor());
            System.out.println("  - Tipo: " + transacao.getTipo());
            System.out.println("  - Categoria: " + transacao.getCategoria());
            System.out.println("  - Status: " + transacao.getStatus());
            System.out.println("  - Data: " + transacao.getData());

            TransacaoFinanceira saved = financeiroService.criarTransacao(transacao);
            System.out.println("Receita salva com ID: " + saved.getId());

            alertSucesso("Receita cadastrada com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            System.err.println("Erro ao salvar receita: " + e.getMessage());
            e.printStackTrace();
            alertErro("Erro ao salvar receita", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        System.out.println("Cancelando criação de receita");
        fecharJanela();
    }

    private void fecharJanela() {
        ((Stage) dataField.getScene().getWindow()).close();
    }

    private boolean validarFormulario() {
        StringBuilder erros = new StringBuilder();

        if (dataField.getValue() == null) erros.append("• Data é obrigatória\n");
        if (descricaoField.getText().isBlank()) erros.append("• Nome / Descrição é obrigatório\n");
        if (valorField.getText().isBlank()) erros.append("• Valor é obrigatório\n");
        if (categoriaCombo.getValue() == null) erros.append("• Categoria é obrigatória\n");
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