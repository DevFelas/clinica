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

/**
 * Controller responsável pela tela de listagem e gerenciamento de pacientes.
 * Gerencia a TableView, permitindo visualização formatada, edição e exclusão de registros.
 */
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

    /**
     * Inicializa o controller configurando as fábricas de células da tabela e
     * carregando os dados iniciais do banco de dados.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        aplicarPermissoesUI(btnFinanceiro, textUsuario);

        configurarColunas();
        atualizarTabela();
    }

    /**
     * Define como cada coluna da TableView deve extrair e exibir os dados do objeto Paciente.
     * Inclui formatação personalizada para CPF, Contato e Datas (padrão brasileiro).
     */
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

    /**
     * Consulta o serviço de pacientes e sincroniza a lista observável com a TableView.
     */
    public void atualizarTabela() {
        List<Paciente> pacientes = pacienteService.listarPacientes();
        listaPacientes.setAll(pacientes);
        tabelaPacientes.setItems(listaPacientes);
    }

    /**
     * Prepara o paciente selecionado para edição através do DataShare e navega para o formulário.
     */
    @FXML
    private void editarPaciente(ActionEvent event) {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            DataShare.setPacienteParaEditar(selecionado);
            navigator.trocarPagina((Node) event.getSource(), "/view/pages/CadastroPaciente.fxml");
        }
    }

    /**
     * Limpa o contexto de edição e navega para a tela de cadastro de novo paciente.
     */
    @FXML
    private void irParaCadPaciente(ActionEvent event) {
        DataShare.limpar();
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/CadastroPaciente.fxml");
    }

    /**
     * Remove o paciente selecionado do sistema via PacienteService e atualiza a interface.
     */
    @FXML
    private void removerPaciente(ActionEvent event) {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            pacienteService.remover(selecionado.getId());
            atualizarTabela();
        }
    }

    /**
     * Trata a ação disparada por um componente da interface
     * e delega a navegação para a implementação da superclasse.
     *
     * <p>Este método é anotado com {@link javafx.fxml.FXML} para ser invocado pelo
     * JavaFX via FXML (ex.: {@code onAction="#irPara"}).</p>
     *
     * @param event o evento de ação gerado pelo JavaFX ao executar a interação do usuário
     */
    @FXML
    public void irPara(ActionEvent event) {
        super.irPara(event);
    }

    /**
     * Trata a ação de saída disparada pela interface (ex.: clique em "Sair")
     * e delega a lógica de logout/encerramento para a implementação da superclasse.
     *
     * <p>Este método é anotado com {@link javafx.fxml.FXML} para ser invocado pelo
     * JavaFX via FXML (ex.: {@code onAction="#sair"}).</p>
     *
     * @param event o evento de ação gerado pelo JavaFX ao executar a interação do usuário
     */
    @FXML
    public void sair(ActionEvent event) {
        super.sair(event);
    }

}