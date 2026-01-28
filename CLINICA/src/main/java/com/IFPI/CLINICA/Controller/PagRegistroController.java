package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Agendamento;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Model.Procedimento;
import com.IFPI.CLINICA.Service.AgendamentoService;
import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Service.ProcedimentoService;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.awt.*;
import java.time.LocalDate;

@Component
public class PagRegistroController {

    private ObservableList<Agendamento> listaAgendamentos;
    private FilteredList<Agendamento> listaFiltrada;

    @Autowired
    private ProcedimentoService procedimentoService;

    @FXML
    private ComboBox<Procedimento> comboProcedimentos;

    @Autowired
    private Navigator navigator;

    @Autowired
    private PacienteService service;

    @Autowired
    private AgendamentoService agendamentoService;

    @FXML
    private TextField campoPesquisa;

    @FXML
    private DatePicker dataInicio;

    @FXML
    private DatePicker dataFim;

    @FXML
    private Button btnNovaConsulta;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnDetalhar;

    @FXML
    private Button btnSair;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<Agendamento> tabelaAgendamentos;

    @FXML
    private TableColumn<Agendamento, String> colPaciente;

    @FXML
    private TableColumn<Agendamento, String> colProcedimento;

    @FXML
    private TableColumn<Agendamento, String> colHorario;

    @FXML
    private TableColumn<Agendamento, LocalDate> colData;

    @FXML
    private TableColumn<Agendamento, String> colStatus;




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
    private void irParaFinaneiro(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Financeiro.fxml"
        );
    }

    // Botão para limpar filtros
    @FXML
    private void limparFiltros() {
        campoPesquisa.clear();
        comboProcedimentos.getSelectionModel().clearSelection();
        dataInicio.setValue(null);
        dataFim.setValue(null);
        listaFiltrada.setPredicate(p -> true);
    }

    @FXML
    private void filtrarPorProcedimento() {
        Procedimento proc = comboProcedimentos.getValue();

        if (proc == null) {
            System.out.println("Nenhum procedimento selecionado");
            return;
        }

        System.out.println(proc.getNome());
    }


    @FXML
    private Label lblTotalRegistros;

    private void atualizarContador() {
        lblTotalRegistros.setText(
                "Total: " + listaFiltrada.size() + " registros"
        );
    }


    @FXML
    public void initialize() {

        // Placeholder para o campo do filtro
        campoPesquisa.setPromptText("Digite o nome do paciente");

        // Placeholder para caso não exiba nenhum registro
        tabelaAgendamentos.setPlaceholder(
                new Label("Nenhum agendamento encontrado")
        );

        // Impedir que as colunas sejam arrastadas
        tabelaAgendamentos.getColumns()
                .forEach(col -> col.setReorderable(false));

        // Lista aberta (ComboBox)
        comboProcedimentos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Procedimento item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome());
                }
            }
        });

        // Item selecionado (ComboBox)
        comboProcedimentos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Procedimento item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("Procedimentos"); // sempre que limpava os filtros isso aqui sumia
                } else {
                    setText(item.getNome());
                }
            }
        });

        tabelaAgendamentos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Ajuste automático das colunas
        tabelaAgendamentos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // MAPEAMENTO DOS CAMPOS (nomes dos atributos da classe Agendamento)
        colPaciente.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getPaciente().getNome()
                )
        );

        colProcedimento.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getProcedimento().getNome()
                )
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        colHorario.setCellValueFactory(cellData -> {
            LocalTime hora = cellData.getValue().getHora();

            return new SimpleStringProperty(
                    hora != null ? hora.format(formatter) : ""
            );
        });



        colData.setCellValueFactory(
                new PropertyValueFactory<>("data")
        );

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        carregarAgendamentos();
        carregarProcedimentos();

        configurarComboBoxProcedimento();
        configurarFiltroNome();
        configurarFiltroProcedimento();
        configurarFiltroData();
    }

    // Esse método será chamado toda vez que algo mudar
    private void aplicarFiltros() {

        String texto = campoPesquisa.getText();
        Procedimento procedimentoSelecionado = comboProcedimentos.getValue();

        listaFiltrada.setPredicate(agendamento -> {

            LocalDate inicio = dataInicio.getValue();
            LocalDate fim = dataFim.getValue();

            // filtro por nome
            if (texto != null && !texto.isBlank()) {
                String nome = agendamento.getPaciente().getNome().toLowerCase();
                if (!nome.contains(texto.toLowerCase())) {
                    return false;
                }
            }

            // filtro por procedimento
            if (procedimentoSelecionado != null) {

                Integer idSelecionado = procedimentoSelecionado.getId();
                Integer idAgendamento = agendamento.getProcedimento().getId();

                if (!idSelecionado.equals(idAgendamento)) {
                    return false;
                }
            }

            // filtro por data
            LocalDate dataAgendamento = agendamento.getData();

            if (inicio != null && dataAgendamento.isBefore(inicio)) {
                return false;
            }

            if (fim != null && dataAgendamento.isAfter(fim)) {
                return false;
            }

            return true;
        });

        lblTotal.setText(listaFiltrada.size() + " registros encontrados");
        atualizarContador();
    }

    private void configurarFiltroNome() {

        campoPesquisa.textProperty().addListener((obs, antigo, novo) -> {
            aplicarFiltros();
        });
    }

    private void configurarFiltroProcedimento() {
        comboProcedimentos.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltros();
        });
    }


    private void configurarFiltroData() {

        dataInicio.valueProperty().addListener((obs, antigo, novo) -> {
            aplicarFiltros();
        });

        dataFim.valueProperty().addListener((obs, antigo, novo) -> {
            aplicarFiltros();
        });
    }

    private void carregarProcedimentos() {
        ObservableList<Procedimento> procedimentos =
                FXCollections.observableArrayList(
                        procedimentoService.listarProcedimentos()
                );

        comboProcedimentos.setItems(procedimentos);
    }

    private void carregarAgendamentos() {
        // Lista de todos os agendamentos
        listaAgendamentos = FXCollections.observableArrayList(
                agendamentoService.listarAgendamentos()
        );

        // Cria lista filtrada para aplicar filtros
        listaFiltrada = new FilteredList<>(listaAgendamentos, p -> true);

        // Cria lista ordenada ligada à lista filtrada
        SortedList<Agendamento> sortedList = new SortedList<>(listaFiltrada);

        // Liga o comparador da tabela ao SortedList
        sortedList.comparatorProperty().bind(tabelaAgendamentos.comparatorProperty());

        // Define a lista ordenada como itens da tabela
        tabelaAgendamentos.setItems(sortedList);

        // Ordenar automaticamente pela data mais recente
        colData.setSortType(TableColumn.SortType.DESCENDING);
        tabelaAgendamentos.getSortOrder().clear();
        tabelaAgendamentos.getSortOrder().add(colData);
    }



    private void configurarComboBoxProcedimento() {
        comboProcedimentos.valueProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal != null) {
                System.out.println(newVal.getId());
                System.out.println(newVal.getNome());
            }
        });
    }


}