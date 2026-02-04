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

/**
 * Controller responsável pela gestão do formulário de pacientes (Cadastro e Edição).
 * Implementa Initializable para configurar o estado inicial da view baseado no contexto de sessão e compartilhamento de dados.
 */
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

    /**
     * Inicializa a interface do usuário:
     * 1. Verifica permissões de acesso (Perfil).
     * 2. Aplica máscaras e filtros de entrada (UX).
     * 3. Determina se a tela operará em modo de Inclusão ou Edição através do DataShare.
     */
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
    /**
     * Adiciona listeners aos campos para garantir que apenas dígitos sejam inseridos
     * e respeitem o limite máximo de caracteres.
     */
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

    /**
     * Garante que o campo aceite apenas caracteres alfabéticos, espaços e acentuação.
     */
    private void configurarCampoApenasLetras(TextField campo) {
        campo.textProperty().addListener((obs, velho, novo) -> {
            // Aceita letras de A-Z, acentuação e espaços
            if (!novo.matches("[A-Za-zÀ-ÿ\\s]*")) {
                campo.setText(novo.replaceAll("[^A-Za-zÀ-ÿ\\s]", ""));
            }
        });
    }


    /**
     * Coleta os dados da interface, realiza a validação de negócio (campos obrigatórios,
     * Regex, tamanhos e lógica de data) e persiste o objeto via PacienteService.
     */
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

    /**
     * Mapeia os dados do objeto Paciente para os campos da tela em caso de edição.
     */
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

    /**
     * Exibe um diálogo de erro padrão para falhas de validação ou exceções.
     */
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
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