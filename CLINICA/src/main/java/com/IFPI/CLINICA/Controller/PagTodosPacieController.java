package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PagTodosPacieController {

    @Autowired
    private Navigator navigator;

    @Autowired
    private PacienteService service;

    @FXML
    private TableView<Paciente> tabelaPacientes;

    @FXML
    private TableColumn<Paciente, String> colNome;

    @FXML
    private TableColumn<Paciente, String> colCpf;

    @FXML
    private TableColumn<Paciente, String> colContato;

    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

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



    // Botões da lateral esquerda da tela

    // Botão para cadastrar um novo paciente
    @FXML
    private void irParaCadPaciente(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/CadasPessoa.fxml"
        );
    }

    @FXML
    public void initialize() {

        tabelaPacientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colContato.setCellValueFactory(new PropertyValueFactory<>("contato"));


        listaPacientes.addAll(
//                new Paciente("Adelson Oliveira Rodrigues", "023.867.934-50", "+55 (89) 9411-7889"),
//                new Paciente("Augusto Carvalho Santos", "001.667.108-70", "+55 (89) 9411-7789"),
//                new Paciente("Alice Celestino Filho", "213.888.647-11", "+55 (89) 9481-9869")
                service.listar()
        );

        tabelaPacientes.setItems(listaPacientes);
    }
}
