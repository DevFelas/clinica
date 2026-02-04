package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Util.DataShare;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public class PagCadPacienteController extends SuperController implements Initializable {

    @Autowired
    private PacienteService service;

    @Autowired
    private Navigator navigator;

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private DatePicker dpNascimento;
    @FXML private TextField txtContato;
    @FXML private TextField txtRua;
    @FXML private TextField txtBairro;
    @FXML private TextField txtCidade;
    @FXML private TextField txtNumero;
    @FXML private Button btnFinanceiro;
    @FXML private Label textUsuario;
    @FXML private Label tituloTela;
    @FXML private Button btnCadastrar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        aplicarPermissoesUI(btnFinanceiro, textUsuario);

        // 2. Travas de Teclado (Filtros em tempo real)
        configurarCampoNumerico(txtCpf, 11);     // CPF: só números, max 11
        configurarCampoNumerico(txtContato, 11); // Contato: só números, max 11
        configurarCampoNumerico(txtNumero, 10);  // Número: só números

        configurarCampoApenasLetras(txtNome);    // Nome: só letras e espaços
        configurarCampoApenasLetras(txtBairro);  // Bairro: só letras e espaços
        configurarCampoApenasLetras(txtCidade);  // Cidade: só letras e espaços

        // 3. Lógica de Edição
        Paciente pacienteParaEditar = DataShare.getPacienteParaEditar();
        if (pacienteParaEditar != null) {
            tituloTela.setText("Editar Paciente");
            btnCadastrar.setText("Salvar");
            preencherCamposParaEdicao(pacienteParaEditar);
        } else {
            tituloTela.setText("Cadastrar Paciente");
            btnCadastrar.setText("Cadastrar");
        }
    }

    // --- MÉTODOS DE FILTRO (UX) ---

    private void configurarCampoNumerico(TextField campo, int max) {
        campo.textProperty().addListener((obs, velho, novo) -> {
            if (!novo.matches("\\d*")) {
                campo.setText(novo.replaceAll("[^\\d]", ""));
            }
            if (campo.getText().length() > max) {
                campo.setText(campo.getText().substring(0, max));
            }
        });
    }

    private void configurarCampoApenasLetras(TextField campo) {
        campo.textProperty().addListener((obs, velho, novo) -> {
            // Aceita letras de A-Z, acentuação e espaços
            if (!novo.matches("[A-Za-zÀ-ÿ\\s]*")) {
                campo.setText(novo.replaceAll("[^A-Za-zÀ-ÿ\\s]", ""));
            }
        });
    }

    // --- AÇÃO PRINCIPAL ---

    @FXML
    private void cadastrarPaciente(ActionEvent event) {
        // Coleta e Limpeza
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String contato = txtContato.getText().trim();
        String rua = txtRua.getText().trim();
        String bairro = txtBairro.getText().trim();
        String cidade = txtCidade.getText().trim();
        String numero = txtNumero.getText().trim();
        LocalDate dataNasc = dpNascimento.getValue();

        // 1. Validação de Campos Vazios
        if (nome.isEmpty() || cpf.isEmpty() || contato.isEmpty() || rua.isEmpty() ||
                bairro.isEmpty() || cidade.isEmpty() || numero.isEmpty() || dataNasc == null) {
            mostrarErro("Todos os campos são obrigatórios!");
            return;
        }

        // 2. Validação de Conteúdo (Nome, Bairro, Cidade)
        if (!nome.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            mostrarErro("O campo Nome deve conter apenas letras.");
            return;
        }
        if (!bairro.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            mostrarErro("O campo Bairro deve conter apenas letras.");
            return;
        }
        if (!cidade.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            mostrarErro("O campo Cidade deve conter apenas letras.");
            return;
        }

        // 3. Validação de Tamanho Exato (CPF e Contato)
        if (cpf.length() != 11) {
            mostrarErro("O CPF deve conter exatamente 11 dígitos.");
            return;
        }
        if (contato.length() != 11) {
            mostrarErro("O contato deve conter 11 dígitos (DDD + 9 + número).");
            return;
        }

        // 4. Validação de Data Futura
        if (dataNasc.isAfter(LocalDate.now())) {
            mostrarErro("A data de nascimento não pode ser maior que a data de hoje.");
            return;
        }

        // --- PROCESSO DE SALVAMENTO ---
        Paciente paciente = DataShare.getPacienteParaEditar();
        boolean isEdicao = (paciente != null);

        if (paciente == null) {
            paciente = new Paciente();
        }

        paciente.setNome(nome);
        paciente.setCpf(cpf);
        paciente.setDataNascimento(dataNasc);
        paciente.setContato(contato);
        paciente.setRua(rua);
        paciente.setBairro(bairro);
        paciente.setCidade(cidade);
        paciente.setNumero(numero);

        try {
            service.cadastrar(paciente);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText(isEdicao ? "Dados do paciente atualizados com sucesso!" : "Paciente cadastrado com sucesso!");
            alert.showAndWait();

            DataShare.limpar();
            irParaPacientes(event);

        } catch (Exception e) {
            mostrarErro("Erro técnico ao salvar: " + e.getMessage());
        }
    }

    private void preencherCamposParaEdicao(Paciente p) {
        txtNome.setText(p.getNome());
        txtCpf.setText(p.getCpf());
        dpNascimento.setValue(p.getDataNascimento());
        txtContato.setText(p.getContato());
        txtRua.setText(p.getRua());
        txtBairro.setText(p.getBairro());
        txtCidade.setText(p.getCidade());
        txtNumero.setText(p.getNumero());
    }

    @FXML private void irParaPacientes(ActionEvent event) { DataShare.limpar(); navigator.trocarPagina((Node) event.getSource(), "/view/pages/Pacientes.fxml"); }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
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