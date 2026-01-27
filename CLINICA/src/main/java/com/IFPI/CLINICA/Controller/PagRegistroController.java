package com.IFPI.CLINICA.Controller;

<<<<<<< HEAD
import com.IFPI.CLINICA.Model.*;
=======
import com.IFPI.CLINICA.Model.Agendamento;
import com.IFPI.CLINICA.Model.Paciente;
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
import com.IFPI.CLINICA.Service.AgendamentoService;
import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Util.Navigator;
<<<<<<< HEAD
import com.IFPI.CLINICA.Util.SessaoUsuario;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
=======
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.awt.*;
import java.time.LocalDate;

@Component
public class PagRegistroController {

    @Autowired
    private Navigator navigator;

    @Autowired
    private PacienteService service;

    @FXML
    private TextField campoPesquisa;

    @FXML
    private ComboBox<String> comboProcedimentos;

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

    @FXML
    private Button btnFinanceiro;

    @FXML
    private Label textUsuario;


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

    @FXML
    private void irParaFinanceiro(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Financeiro.fxml"
        );
    }


    @FXML
    public void initialize() {

<<<<<<< HEAD
        Usuario usuario = SessaoUsuario.getInstance().getUsuarioLogado();

        if (usuario.getPerfil() == Perfil.RECEPCIONISTA) {
            btnFinanceiro.setVisible(false);
            textUsuario.setText("RECEPCIONISTA");
        }

        if (usuario.getPerfil() == Perfil.ADMIN) {
            textUsuario.setText("ADMINISTRADOR");
        }

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

=======
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
        // Ajuste automático das colunas
        tabelaAgendamentos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // MAPEAMENTO DOS CAMPOS (nomes dos atributos da classe Agendamento)
        colPaciente.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getPaciente().getNome()
                )
        );


        colProcedimento.setCellValueFactory(
                new PropertyValueFactory<>("procedimento")
        );

        colHorario.setCellValueFactory(
                new PropertyValueFactory<>("horario")
        );

        colData.setCellValueFactory(
                new PropertyValueFactory<>("data")
        );

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );


    }
<<<<<<< HEAD

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

    @FXML
    private void sair(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Sair do sistema");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente sair do sistema?");

        confirm.showAndWait().ifPresent(resposta -> {

            if (resposta == ButtonType.OK) {

                // limpa sessão
                SessaoUsuario.getInstance().limparSessao();

                // volta para login
                navigator.trocarPagina(
                        (Node) event.getSource(),
                        "/view/pages/Login.fxml"
                );
            }
        });
    }

=======
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
}