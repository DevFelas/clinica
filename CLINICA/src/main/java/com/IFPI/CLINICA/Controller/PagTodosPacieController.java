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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.collections.transformation.FilteredList;


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

    @FXML
    private TableColumn<Paciente, String> colNasc;

    @FXML
    private TableColumn<Paciente, String> colCidade;

    @FXML
    private TableColumn<Paciente, String> colBairro;

    @FXML
    private TableColumn<Paciente, String> colRua;

    @FXML
    private TableColumn<Paciente, String> colNum;

    @FXML
    private TextField campoBusca;

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

    // Botão para ir para tela Financeiro (Descomentar quando a tela existir
    @FXML
    private void irParaFinaneiro(ActionEvent event) {
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
        colNasc.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colCidade.setCellValueFactory(new PropertyValueFactory<>("cidade"));
        colBairro.setCellValueFactory(new PropertyValueFactory<>("bairro"));
        colRua.setCellValueFactory(new PropertyValueFactory<>("rua"));
        colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));

        listaPacientes.clear();

        listaPacientes.addAll(
//                new Paciente("Adelson Oliveira Rodrigues", "023.867.934-50", "+55 (89) 9411-7889", "11/01/1111", "Jacobina", "Aquele", "Aquela", "1"),
//                new Paciente("Augusto Carvalho Santos", "001.667.108-70", "+55 (89) 9411-7789", "22/2/2222", ),
//                new Paciente("Alice Celestino Filho", "213.888.647-11", "+55 (89) 9481-9869")
                service.listar()
        );

        FilteredList<Paciente> listaFiltrada = new FilteredList<>(listaPacientes, p -> true);
        campoBusca.textProperty().addListener((obs, valorAntigo, valorNovo) -> {

            String filtro = valorNovo.toLowerCase();

            listaFiltrada.setPredicate(paciente -> {

                if (filtro == null || filtro.isEmpty()) {
                    return true; // mostra todos
                }

                // Filtra por nome, cpf ou contato
                if (paciente.getNome() != null && paciente.getNome().toLowerCase().contains(filtro)) {
                    return true;
                }

                if (paciente.getCpf() != null && paciente.getCpf().toLowerCase().contains(filtro)) {
                    return true;
                }

                if (paciente.getContato() != null && paciente.getContato().toLowerCase().contains(filtro)) {
                    return true;
                }

                return false;
            });
        });


        tabelaPacientes.setItems(listaFiltrada);
    }
}
