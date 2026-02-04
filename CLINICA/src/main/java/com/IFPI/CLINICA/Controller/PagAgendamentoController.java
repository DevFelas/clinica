package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Service.AgendamentoService;
import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Service.ProcedimentoService;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class PagAgendamentoController extends SuperController implements Initializable {

    @Autowired private Navigator navigator;
    @Autowired private AgendamentoService agendamentoService;
    @Autowired private ProcedimentoService procedimentoService;
    @Autowired private PacienteService pacienteService;

    @FXML private TextField cpfField;
    @FXML private ComboBox<Procedimento> procedimentoCombo;
    @FXML private ComboBox<LocalTime> horarioCombo;
    @FXML private DatePicker dataPicker;
    @FXML private Button btnFinanceiro;
    @FXML private Label textUsuario;

    private Optional<Paciente> pacienteEncontrado = Optional.empty();


    private static final LocalTime ALMOCO_INICIO = LocalTime.of(12, 0);
    private static final LocalTime ALMOCO_FIM = LocalTime.of(14, 0);
    private static final LocalTime EXPEDIENTE_FIM = LocalTime.of(18, 0);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        aplicarPermissoesUI(btnFinanceiro, textUsuario);

        procedimentoCombo.setItems(FXCollections.observableArrayList(procedimentoService.listarProcedimentos()));

        // Horários disponíveis (mantive os originais, mas pode expandir se quiser)
        dataPicker.valueProperty().addListener((obs, o, n) -> atualizarHorarios());
        procedimentoCombo.valueProperty().addListener((obs, o, n) -> atualizarHorarios());


    }

    @FXML private void irParaAgenda() { navigator.trocarPagina(cpfField,"/view/pages/Agenda.fxml"); }

    @FXML
    private void onAgendar() {
        if (!validarFormulario()){
            return;
        }

        String cpf = cpfField.getText().replaceAll("\\D", "");
        pacienteEncontrado = pacienteService.buscarPorCpf(cpf);
        if (pacienteEncontrado == null) {
            mostrarAlerta("Paciente não encontrado para o CPF informado.");
            return;
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(StatusAgendamento.AGENDADA);
        pacienteEncontrado.ifPresent(paciente -> {agendamento.setPaciente(paciente);});
        agendamento.setProcedimento(procedimentoCombo.getValue());
        agendamento.setData(dataPicker.getValue());
        agendamento.setHora(horarioCombo.getValue());

        agendamentoService.marcarAgendamento(agendamento);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Agendamento realizado com sucesso!");
        alert.showAndWait();

        irParaAgenda();
    }

    private boolean validarFormulario() {
        String cpf = cpfField.getText();

        if (cpf == null || cpf.isBlank()) {
            mostrarAlerta("Informe o CPF do paciente.");
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (!cpf.matches("\\d{11}")) {
            mostrarAlerta("CPF inv├ílido. Deve conter 11 n├║meros.");
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
            mostrarAlerta("Selecione um hor├írio.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Valida├º├úo");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

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

        List<Agendamento> agendados =
                agendamentoService.buscarPorDataEStatus(
                        data,
                        List.of(
                                StatusAgendamento.AGENDADA,
                                StatusAgendamento.REALIZADA
                        )
                );


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

        pacienteService.buscarPorCpf(cpf).ifPresentOrElse(
                usuario -> {
                    // AQUI você usa os dados do paciente
                    System.out.println("Paciente encontrado: " + usuario.getNome());

                    // Exemplo se tivesse campos:
                    // nomeField.setText(usuario.getNome());
                    // telefoneField.setText(usuario.getTelefone());
                },
                () -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Paciente não encontrado");
                    alert.setHeaderText(null);
                    alert.setContentText("Paciente não cadastrado. Deseja cadastrar agora?");

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
    public void irPara(ActionEvent event) {
        super.irPara(event);
    }

    @FXML
    public void sair(ActionEvent event) {
        super.sair(event);
    }

}
