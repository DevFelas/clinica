package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Agendamento;
import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Service.AgendamentoService;
import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Util.Navigator;
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
//    @FXML
//    private void irParaFinaneiro(ActionEvent event) {
//        navigator.trocarPagina(
//                (Node) event.getSource(),
//                "/view/pages/Financeiro.fxml"
//        );
//    }

    @FXML
    public void initialize() {

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
}