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
public class FinanceiroController implements Initializable {

    @Autowired
    private Navigator navigator;

    // Campos editáveis do resumo financeiro
    @FXML private TextField totalReceitasField;
    @FXML private TextField lucroField;
    @FXML private TextField totalDespesasField;

    // Campos editáveis dos procedimentos
    @FXML private TextField consultaField;
    @FXML private TextField limpezaField;
    @FXML private TextField exodontiaField;
    @FXML private TextField proteseField;
    @FXML private TextField implanteField;
    @FXML private TextField manutencaoField;
    @FXML private TextField montagemField;
    @FXML private TextField restauracaoField;

    // Campos de data
    @FXML private DatePicker dataInicioPicker;
    @FXML private DatePicker dataFimPicker;

    // Tabela editável
    @FXML private TableView<?> tabelaFinanceiro;
    @FXML private TableColumn<?, ?> colData;
    @FXML private TableColumn<?, ?> colPaciente;
    @FXML private TableColumn<?, ?> colCategoria;
    @FXML private TableColumn<?, ?> colValor;
    @FXML private TableColumn<?, ?> colTipo;
    @FXML private TableColumn<?, ?> colStatus;

    // Informações do paciente (somente exibição)
    @FXML private Label infoNome;
    @FXML private Label infoCpf;
    @FXML private Label infoProcedimento;
    @FXML private Label infoDataProc;
    @FXML private Label infoValor;
    @FXML private Label infoStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar a tabela para ser editável
        tabelaFinanceiro.setEditable(true);

        // Configurar listeners para quando uma linha for selecionada
        tabelaFinanceiro.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Aqui você atualizaria as labels com os dados da linha selecionada
                        // Exemplo: infoNome.setText(newValue.getNome());
                    }
                });

        // Configurar os campos para aceitar apenas números e formatação monetária
        configurarCamposMonetarios();
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

    private void configurarCamposMonetarios() {
        // Adicionar máscara para campos monetários
        TextField[] camposMonetarios = {
                totalReceitasField, lucroField, totalDespesasField,
                consultaField, limpezaField, exodontiaField, proteseField,
                implanteField, manutencaoField, montagemField, restauracaoField
        };

        for (TextField campo : camposMonetarios) {
            campo.textProperty().addListener((observable, oldValue, newValue) -> {
                // Validação básica para formato monetário
                if (!newValue.matches("^R\\$\\s?\\d{1,3}(\\.\\d{3})*(,\\d{2})?$")) {
                    // Aqui você pode implementar formatação automática
                }
            });
        }
    }

    @FXML
    private void onSalvarAlteracoes() {
        // Método para salvar as alterações dos campos editáveis
        System.out.println("Alterações salvas!");
        // Aqui você implementaria a lógica para salvar no banco de dados
    }
}