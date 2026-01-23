package com.IFPI.CLINICA.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;


@Component
public class PagtodosPacieController {

    @FXML
    private TableView<Paciente> tabelaPacientes;

    @FXML
    private TableColumn<Paciente, String> colNome;

    @FXML
    private TableColumn<Paciente, String> colCpf;

    @FXML
    private TableColumn<Paciente, String> colContato;

    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        tabelaPacientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colContato.setCellValueFactory(new PropertyValueFactory<>("contato"));


        listaPacientes.addAll(
                new Paciente("Adelson Oliveira Rodrigues", "023.867.934-50", "+55 (89) 9411-7889"),
                new Paciente("Augusto Carvalho Santos", "001.667.108-70", "+55 (89) 9411-7789"),
                new Paciente("Alice Celestino Filho", "213.888.647-11", "+55 (89) 9481-9869")
        );

        tabelaPacientes.setItems(listaPacientes);
    }
}
