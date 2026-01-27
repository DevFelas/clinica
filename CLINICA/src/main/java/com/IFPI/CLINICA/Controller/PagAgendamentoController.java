package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Util.Navigator;
<<<<<<< HEAD
import com.IFPI.CLINICA.Util.SessaoUsuario;
import javafx.collections.FXCollections;
=======
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class PagAgendamentoController implements Initializable {

    @Autowired
    private Navigator navigator;

    @FXML
    private TextField cpfField;

    @FXML
    private ComboBox<String> procedimentoCombo;

    @FXML
    private ComboBox<String> horarioCombo;

    @FXML
    private DatePicker dataPicker;

<<<<<<< HEAD
    private Paciente pacienteEncontrado;

    @FXML
    private Button btnFinanceiro;

    @FXML
    private Label textUsuario;

    private static final LocalTime ALMOCO_INICIO = LocalTime.of(12, 0);
    private static final LocalTime ALMOCO_FIM = LocalTime.of(14, 0);
    private static final LocalTime EXPEDIENTE_FIM = LocalTime.of(18, 0);

=======
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Usuario usuario = SessaoUsuario.getInstance().getUsuarioLogado();

        if (usuario.getPerfil() == Perfil.RECEPCIONISTA) {
            btnFinanceiro.setVisible(false);
            textUsuario.setText("RECEPCIONISTA");
        }

        if (usuario.getPerfil() == Perfil.ADMIN) {
            textUsuario.setText("ADMINISTRADOR");
        }

        // ADICIONE TODOS OS 8 PROCEDIMENTOS DO FINANCEIRO
        procedimentoCombo.getItems().addAll(
                "Consulta",
                "Limpeza",
                "Exodontia",
                "Prótese",
                "Implante",
                "Manutenção de Aparelho",
                "Montagem de aparelho",
                "Restauração"
        );

        // Horários disponíveis (mantive os originais, mas pode expandir se quiser)
<<<<<<< HEAD
        dataPicker.valueProperty().addListener((obs, o, n) -> atualizarHorarios());
        procedimentoCombo.valueProperty().addListener((obs, o, n) -> atualizarHorarios());


=======
        horarioCombo.getItems().addAll(
                "08:00",
                "09:00",
                "10:00"
        );
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
    }

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
    private void irParaFinanceiro(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Financeiro.fxml"
        );
    }

    @FXML
    private void onAgendar() {
        if (!validarFormulario()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Agendamento");
        alert.setHeaderText(null);
        alert.setContentText("Agendamento validado com sucesso.");
        alert.showAndWait();
    }

    private boolean validarFormulario() {
        String cpf = cpfField.getText();

        if (cpf == null || cpf.isBlank()) {
            mostrarAlerta("Informe o CPF do paciente.");
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (!cpf.matches("\\d{11}")) {
            mostrarAlerta("CPF inválido. Deve conter 11 números.");
            return false;
        }

        if (procedimentoCombo.getValue() == null) {
            mostrarAlerta("Selecione um procedimento.");
            return false;
        }

        if (dataPicker.getValue() == null) {
            mostrarAlerta("Selecione uma data.");
            return false;
        }

        if (horarioCombo.getValue() == null) {
            mostrarAlerta("Selecione um horário.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
<<<<<<< HEAD

    private List<LocalTime> gerarHorariosBase() {

        List<LocalTime> horarios = new ArrayList<>();

        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(18, 0);

        while (inicio.isBefore(fim)) {
            horarios.add(inicio);
            inicio = inicio.plusMinutes(30);
        }

        return horarios;
    }

    private boolean temConflito(LocalTime inicioNovo, LocalTime fimNovo, Agendamento existente ) {
        LocalTime inicioExistente = existente.getHora();
        LocalTime fimExistente = calcularHoraFim(existente);

        return inicioNovo.isBefore(fimExistente) && fimNovo.isAfter(inicioExistente);
    }

    private void atualizarHorarios() {

        LocalDate data = dataPicker.getValue();
        Procedimento proc = procedimentoCombo.getValue();

        if (data == null || proc == null) {
            horarioCombo.getItems().clear();
            return;
        }

        int duracao =
                proc.getTempo_previsto()
                        .toLocalTime()
                        .getHour() * 60
                        +
                        proc.getTempo_previsto()
                                .toLocalTime()
                                .getMinute();

        List<LocalTime> todos = gerarHorariosBase();

        horarioCombo.setItems(
                FXCollections.observableArrayList(todos)
        );

        horarioCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(item.toString());

                    boolean disponivel =
                            horarioEstaDisponivel(item, data, duracao);

                    setDisable(!disponivel);

                    if (!disponivel) {
                        setStyle("-fx-text-fill: gray;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Para o bot├úo do ComboBox
        horarioCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
    }


    private LocalTime calcularHoraFim(Agendamento ag) {

        int minutos =
                ag.getProcedimento()
                        .getTempo_previsto()
                        .toLocalTime()
                        .getHour() * 60
                        +   ag.getProcedimento()
                        .getTempo_previsto()
                        .toLocalTime()
                        .getMinute();

        return ag.getHora().plusMinutes(minutos);
    }

    private boolean horarioEstaDisponivel(
            LocalTime inicio,
            LocalDate data,
            int duracaoMinutos
    ) {
        LocalTime fim = inicio.plusMinutes(duracaoMinutos);

        if (fim.isAfter(EXPEDIENTE_FIM)) {
            return false;
        }

        // horário de almoço
        boolean conflitaComAlmoco = inicio.isBefore(ALMOCO_FIM) && fim.isAfter(ALMOCO_INICIO);

        if (conflitaComAlmoco) {
            return false;
        }

        List<Agendamento> agendados = agendamentoRepository.findByData(data);

        for (Agendamento ag : agendados) {
            if (temConflito(inicio, fim, ag)) {
                return false;
            }
        }

        return true;
    }

    @FXML
    private void buscarPacientePorCpf() {

        String cpf = cpfField.getText();

        if (cpf == null || cpf.isBlank()) {
            mostrarAlerta("Informe o CPF.");
            return;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (!cpf.matches("\\d{11}")) {
            mostrarAlerta("CPF inválido.");
            return;
        }

        pacienteRepository.findByCpf(cpf).ifPresentOrElse(
                usuario -> {
                    // AQUI voc├¬ usa os dados do paciente
                    System.out.println("Paciente encontrado: " + usuario.getNome());

                    // Exemplo se tivesse campos:
                    // nomeField.setText(usuario.getNome());
                    // telefoneField.setText(usuario.getTelefone());
                },
                () -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Paciente n├úo encontrado");
                    alert.setHeaderText(null);
                    alert.setContentText("Paciente n├úo cadastrado. Deseja cadastrar agora?");

                    alert.showAndWait().ifPresent(resposta -> {
                        if (resposta == ButtonType.OK) {
                            navigator.trocarPagina(
                                    cpfField,
                                    "/view/pages/Registro.fxml"
                            );
                        }
                    });
                }
        );
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

}
=======
}
>>>>>>> 4e4bf16dc8fc12a20f0fdabc70203932c733c2b2
