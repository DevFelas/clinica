package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.CategoriaTransacao;
import com.IFPI.CLINICA.Model.StatusTransacao;
import com.IFPI.CLINICA.Model.TransacaoFinanceira;
import com.IFPI.CLINICA.Model.TipoTransacao;
import com.IFPI.CLINICA.Service.FinanceiroService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
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
        System.out.println("=== NOVA DESPESA DIALOG INITIALIZE ===");

        dataField.setValue(LocalDate.now());

        categoriaCombo.setItems(categorias);
        categoriaCombo.setValue(CategoriaTransacao.INSUMOS);

        statusCombo.setItems(statusList);
        statusCombo.setValue(StatusTransacao.PENDENTE);

        configurarMascaraValor();

        categoriaCombo.setOnAction(event -> {
            if (categoriaCombo.getValue() != null) {
                descricaoField.setText("Despesa - " + categoriaCombo.getValue().getDescricao());
                System.out.println("Descrição preenchida: " + descricaoField.getText());
            }
        });

        System.out.println("Diálogo de nova despesa configurado com sucesso");
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
        System.out.println("=== TENTANDO SALVAR NOVA DESPESA ===");
        if (!validarFormulario()) {
            System.out.println("Validação do formulário falhou");
            return;
        }

        try {
            // Converter valor para BigDecimal
            String valorTexto = valorField.getText().replace(",", ".");
            if (valorTexto.isEmpty()) {
                valorTexto = "0.00";
            }

            TransacaoFinanceira transacao = new TransacaoFinanceira();
            transacao.setData(dataField.getValue());
            transacao.setDataCadastro(LocalDateTime.now());
            transacao.setDescricao(descricaoField.getText());
            transacao.setCategoria(categoriaCombo.getValue());
            transacao.setValor(new BigDecimal(valorTexto));
            transacao.setStatus(statusCombo.getValue());
            transacao.setTipo(TipoTransacao.SAIDA);

            System.out.println("Salvando despesa:");
            System.out.println("  - Descrição: " + transacao.getDescricao());
            System.out.println("  - Valor: " + transacao.getValor());
            System.out.println("  - Tipo: " + transacao.getTipo());
            System.out.println("  - Categoria: " + transacao.getCategoria());
            System.out.println("  - Status: " + transacao.getStatus());

            financeiroService.criarTransacao(transacao);

            alertSucesso("Despesa cadastrada com sucesso!");

            // Fechar a janela
            Stage stage = (Stage) dataField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("Erro ao salvar despesa: " + e.getMessage());
            e.printStackTrace();
            alertErro("Erro ao salvar despesa", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        System.out.println("Cancelando criação de despesa");
        Stage stage = (Stage) dataField.getScene().getWindow();
        stage.close();
    }

    private boolean validarFormulario() {
        StringBuilder erros = new StringBuilder();

        if (dataField.getValue() == null) {
            erros.append("• Data é obrigatória\n");
            System.out.println("Data não preenchida");
        }

        if (descricaoField.getText() == null || descricaoField.getText().isBlank()) {
            erros.append("• Descrição é obrigatória\n");
            System.out.println("Descrição não preenchida");
        }

        if (categoriaCombo.getValue() == null) {
            erros.append("• Categoria é obrigatória\n");
            System.out.println("Categoria não selecionada");
        }

        if (valorField.getText() == null || valorField.getText().isBlank()) {
            erros.append("• Valor é obrigatório\n");
            System.out.println("Valor não preenchido");
        }

        if (statusCombo.getValue() == null) {
            erros.append("• Status é obrigatório\n");
            System.out.println("Status não selecionado");
        }

        if (!erros.isEmpty()) {
            alertAviso("Validação", erros.toString());
            return false;
        }

        System.out.println("Validação do formulário OK");
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