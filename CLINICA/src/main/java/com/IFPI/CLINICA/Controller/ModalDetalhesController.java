package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Repository.ProcedimentoRepository;
import com.IFPI.CLINICA.Service.AgendamentoService;
import com.IFPI.CLINICA.Service.FinanceiroService;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ModalDetalhesController {

    @Autowired private AgendamentoService agendamentoService;
    @Autowired private FinanceiroService financeiroService;
    @Autowired private ProcedimentoRepository procedimentoRepository;

    private Agendamento agendamento;
    private ModoTelaAgendamento modo;

    @FXML private TextField txtPaciente;
    @FXML private TextField txtContato;
    @FXML private TextField txtCpf;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Procedimento> cbProcedimento;
    @FXML private ComboBox<LocalTime> cbHorario;
    @FXML private TextField txtValor;
    @FXML private Button btnSalvar;
    @FXML private Button btnRealizado;

    private static final LocalTime ALMOCO_INICIO = LocalTime.of(12, 0);
    private static final LocalTime ALMOCO_FIM = LocalTime.of(14, 0);
    private static final LocalTime EXPEDIENTE_FIM = LocalTime.of(18, 0);

    private boolean alterou = false;
    public boolean isAlterou() {
        return alterou;
    }


    public void configurar(Agendamento agendamento, ModoTelaAgendamento modo) {
        this.agendamento = agendamento;
        this.modo = modo;

        preencherCampos();
        cbProcedimento.valueProperty()
                .addListener((obs, o, n) -> atualizarHorariosModal());

        datePicker.valueProperty()
                .addListener((obs, o, n) -> atualizarHorariosModal());

        cbProcedimento.setItems(
                FXCollections.observableArrayList(
                        procedimentoRepository.findAll()
                )
        );
        cbProcedimento.valueProperty().addListener((obs, oldProc, newProc) -> {
            if (newProc != null) {
                txtValor.setText(
                        String.format("R$ %.2f", newProc.getValor())
                );
            } else {
                txtValor.clear();
            }
        });
        atualizarHorariosModal();
        configurarModo();
    }

    private void preencherCampos() {
        txtPaciente.setText(agendamento.getPaciente().getNome());
        txtCpf.setText(agendamento.getPaciente().getCpf());
        txtContato.setText(agendamento.getPaciente().getContato());

        datePicker.setValue(agendamento.getData());
        cbProcedimento.setValue(agendamento.getProcedimento());
        txtValor.setText(
                String.format("R$ %.2f", agendamento.getProcedimento().getValor())
        );
    }

    private void configurarModo() {
        boolean edicao = modo == ModoTelaAgendamento.EDICAO;

        btnSalvar.setVisible(edicao);
        btnRealizado.setVisible(!edicao);
        txtPaciente.setDisable(true);
        txtCpf.setDisable(true);
        txtContato.setDisable(true);
        datePicker.setDisable(!edicao);
        cbHorario.setDisable(!edicao);
        cbProcedimento.setDisable(!edicao);
    }

    @FXML
    private void fecharModal(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
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

    private LocalTime calcularHoraFim(LocalTime inicio, Procedimento proc) {

        int minutos =
                proc.getTempo_previsto()
                        .toLocalTime()
                        .getHour() * 60
                        +
                        proc.getTempo_previsto()
                                .toLocalTime()
                                .getMinute();

        return inicio.plusMinutes(minutos);
    }

    private boolean temConflito(
            LocalTime inicioNovo,
            LocalTime fimNovo,
            Agendamento existente
    ) {

        // 🔥 ignora o próprio agendamento (modo edição)
        if (modo == ModoTelaAgendamento.EDICAO &&
                existente.getId().equals(agendamento.getId())) {
            return false;
        }

        LocalTime inicioExistente = existente.getHora();
        LocalTime fimExistente =
                calcularHoraFim(inicioExistente, existente.getProcedimento());

        return inicioNovo.isBefore(fimExistente) &&
                fimNovo.isAfter(inicioExistente);
    }

    private void atualizarHorariosModal() {

        LocalDate data = datePicker.getValue();
        Procedimento proc = cbProcedimento.getValue();

        if (data == null || proc == null) {
            cbHorario.getItems().clear();
            return;
        }

        List<LocalTime> horarios = gerarHorariosBase();

        cbHorario.setItems(FXCollections.observableArrayList(horarios));

        cbHorario.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(item.toString());

                    boolean disponivel =
                            horarioDisponivelModal(item, data, proc);

                    setDisable(!disponivel);

                    if (!disponivel) {
                        setStyle("-fx-text-fill: gray;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        cbHorario.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });


        cbHorario.setValue(agendamento.getHora());

    }

    private boolean horarioDisponivelModal(
            LocalTime inicio,
            LocalDate data,
            Procedimento proc
    ) {

        LocalTime fim = calcularHoraFim(inicio, proc);

        // expediente
        if (fim.isAfter(EXPEDIENTE_FIM)) {
            return false;
        }

        // almoço
        boolean conflitaAlmoco =
                inicio.isBefore(ALMOCO_FIM) && fim.isAfter(ALMOCO_INICIO);

        if (conflitaAlmoco) {
            return false;
        }

        List<Agendamento> agendados = agendamentoService.buscarPorData(data);

        for (Agendamento ag : agendados) {
            if (temConflito(inicio, fim, ag)) {
                return false;
            }
        }

        return true;
    }

    @FXML
    private void salvar() {

        agendamento.setData(datePicker.getValue());
        agendamento.setHora(cbHorario.getValue());
        agendamento.setProcedimento(cbProcedimento.getValue());

        agendamentoService.marcarAgendamento(agendamento);

        alterou = true;

        Stage stage = (Stage) txtPaciente.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void concluir() {

        TransacaoFinanceira transacao = new TransacaoFinanceira();
        transacao.setDescricao(agendamento.getProcedimento().toString());
        transacao.setValor(agendamento.getProcedimento().getValor());
        transacao.setData(agendamento.getData());
        transacao.setTipo(TipoTransacao.ENTRADA);
        transacao.setStatus(StatusTransacao.PAGO);
        transacao.setPaciente(agendamento.getPaciente());

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Concluir Agendamento");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja marcar este agendamento como CONCLUÍDO?");

        confirm.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {

                agendamento.setStatus(com.IFPI.CLINICA.Model.StatusAgendamento.REALIZADA);
                agendamentoService.marcarAgendamento(agendamento);
                financeiroService.criarTransacao(transacao);
                alterou = true;

                // fecha o modal
                Stage stage = (Stage) btnSalvar.getScene().getWindow();
                stage.close();
            }
        });
    }

}