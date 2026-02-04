package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Util.DataShare;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class PagPacientesController extends SuperController implements Initializable {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private Navigator navigator;

    @FXML private Label textUsuario;
    @FXML private Button btnFinanceiro;
    @FXML private TextField campoBusca;
    @FXML private TableView<Paciente> tabelaPacientes;

    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colCpf;
    @FXML private TableColumn<Paciente, String> colContato;
    @FXML private TableColumn<Paciente, String> colNasc;
    @FXML private TableColumn<Paciente, String> colCidade;
    @FXML private TableColumn<Paciente, String> colBairro;
    @FXML private TableColumn<Paciente, String> colRua;
    @FXML private TableColumn<Paciente, String> colNum;

    private final ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        aplicarPermissoesUI(btnFinanceiro, textUsuario);

        configurarColunas();
        atualizarTabela();
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        // CPF e Contato formatados (usando os métodos do seu Model)
        colCpf.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpfFormatado()));
        colContato.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContatoFormatado()));

        // Formatação da data brasileira
        DateTimeFormatter formatoBra = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colNasc.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataNascimento() != null) {
                String dataFormatada = cellData.getValue().getDataNascimento().format(formatoBra);
                return new SimpleStringProperty(dataFormatada);
            }
            return new SimpleStringProperty("");
        });


        if (colCidade != null) colCidade.setCellValueFactory(new PropertyValueFactory<>("cidade"));
        if (colBairro != null) colBairro.setCellValueFactory(new PropertyValueFactory<>("bairro"));
        if (colRua != null) colRua.setCellValueFactory(new PropertyValueFactory<>("rua"));
        if (colNum != null) colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));
    }

    public void atualizarTabela() {
        List<Paciente> pacientes = pacienteService.listarPacientes();
        listaPacientes.setAll(pacientes);
        tabelaPacientes.setItems(listaPacientes);
    }

    @FXML
    private void editarPaciente(ActionEvent event) {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            DataShare.setPacienteParaEditar(selecionado);
            navigator.trocarPagina((Node) event.getSource(), "/view/pages/CadastroPaciente.fxml");
        }
    }

    @FXML
    private void irParaCadPaciente(ActionEvent event) {
        DataShare.limpar();
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/CadastroPaciente.fxml");
    }

    @FXML
    private void removerPaciente(ActionEvent event) {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            pacienteService.remover(selecionado.getId());
            atualizarTabela();
        }
    }

    @FXML
    public void irPara(ActionEvent event) {
        super.irPara(event);
    }

    @FXML
    public void sair(ActionEvent event) {
        super.sair(event);
    }

}