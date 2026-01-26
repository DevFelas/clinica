package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Agendamento;
import com.IFPI.CLINICA.Model.Perfil;
import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Repository.AgendamentoRepository;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import javafx.event.ActionEvent;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class PaginaInicialAgendaController implements Initializable{

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private Navigator navigator;

    @FXML
    private GridPane agendaGrid;

    @FXML
    private Button btnFinanceiro;

    @FXML
    private Label textUsuario;

    @FXML
    private DatePicker datePicker;

    private static final LocalTime ALMOCO_INICIO = LocalTime.of(12, 0);
    private static final LocalTime ALMOCO_FIM = LocalTime.of(14, 0);


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

        semanaInicio = LocalDate.now().with(DayOfWeek.MONDAY); // Pega a segunda-feira da semana atual
        semanaFim = semanaInicio.plusDays(6); //incrementa 6 dias


        montarAgenda();
        renderizarAlmoco();
        carregarAgendamentos();

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                atualizarSemana(newDate);
            }
        });
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

    // Botão para ir para tela Financeiro
    @FXML
    private void irParaFinanceiro(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Financeiro.fxml"
        );
    }

    // BOTÕES DA LATERAL ESQUERDA

    // Botão para ir para tela novo agendamento//
    @FXML
    private void irParaNovoAgendamento(ActionEvent event) {
        navigator.trocarPagina(
                (Node) event.getSource(),
                "/view/pages/Agendamento.fxml"
        );
    }

    // Botão para ir para tela Editar
//    @FXML
//    private void irParaEditar(ActionEvent event) {
//        navigator.trocarPagina(
//                (Node) event.getSource(),
//                "/view/pages/CadasPessoa.fxml"
//        );
//    }

    // Botão para ir para tela Cancelar
    //@FXML
//    private void irParaCancelar(ActionEvent event) {
//        navigator.trocarPagina(
//                (Node) event.getSource(),
//                "/view/pages/CadasPessoa.fxml"
//        );
//    }

    // Botão para ir para tela Detalhar
//    @FXML
//    private void irParaDetalhar(ActionEvent event) {
//        navigator.trocarPagina(
//                (Node) event.getSource(),
//                "/view/pages/CadasPessoa.fxml"
//        );
//    }


    private void montarAgenda() {
        agendaGrid.getChildren().clear();
        agendaGrid.getColumnConstraints().clear();
        agendaGrid.getRowConstraints().clear();
        agendaGrid.setGridLinesVisible(false);

        String[] dias = new String[7];
        dias[0] = "08:00";

        for (int i = 1; i <= 6; i++) {
            LocalDate dia = semanaInicio.plusDays(i - 1);
            dias[i] = dia.getDayOfWeek().getDisplayName(
                    TextStyle.FULL,
                    new Locale("pt", "BR")
            ) + "\n" + dia.format(DateTimeFormatter.ofPattern("dd/MM"));
        }


        for (int col = 0; col < dias.length; col++) {

            Label label = new Label(dias[col]);
            label.setPrefHeight(40);
            label.setMaxWidth(Double.MAX_VALUE);

            if (col == 0) {
                // Cabeçalho da coluna de horários
                label.setAlignment(Pos.BOTTOM_RIGHT);
                label.setPadding(new Insets(0, 10, 0, 0));

                label.setStyle("""
                    -fx-padding: 2 10 0 0;
                    -fx-background-color: #F2F3F4;
                    -fx-border-color: #fff;
                    -fx-border-width: 0 0 0 0;
                    -fx-font-size: 10px;
                        """);
            } else {
                // Cabeçalho dos dias
                label.setAlignment(Pos.CENTER);

                label.setStyle("""
                    -fx-background-color: #A5D6C1;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-border-color: #D6EAF8;
                    -fx-border-width: 0 1 1 0;
                """);
            }

            agendaGrid.add(label, col, 0);
        }



        List<String> horarios = gerarHorarios();

        int row = 1;

        for (String hora : horarios) {

            Label horaLabel = new Label(hora);

            horaLabel.setMinHeight(80);
            horaLabel.setPrefHeight(80);
            horaLabel.setMaxHeight(80);

            horaLabel.setAlignment(Pos.BOTTOM_RIGHT);

            horaLabel.setPrefWidth(80);
            horaLabel.setMaxWidth(Double.MAX_VALUE);

            horaLabel.setStyle("""
                -fx-padding: 2 10 0 0;
                -fx-background-color: #F2F3F4;
                -fx-border-color: #fff;
                -fx-border-width: 0 0 0 0;
                -fx-font-size: 10px;
            """);

            agendaGrid.add(horaLabel, 0, row);
            GridPane.setValignment(horaLabel, VPos.BOTTOM);
            GridPane.setHalignment(horaLabel, HPos.RIGHT);


            for (int col = 1; col <= 6; col++) {
                Pane cell = new Pane();
                cell.setMinHeight(80);
                cell.setPrefHeight(80);

                cell.setStyle("""
                    -fx-background-color: transparent;
                    -fx-font-size: 10px;
                    -fx-background-color: white;
                    -fx-border-color: #D5D8DC;
                    -fx-border-width: 0 1 1 0;
                """);

                agendaGrid.add(cell, col, row);
            }

            row++;
        }

        configurarColunasELinhas(row);
    }

    private List<String> gerarHorarios() {
        List<String> horarios = new ArrayList<>();


        LocalTime time = LocalTime.of(8, 30);
        LocalTime fimDia = LocalTime.of(18, 0);

        while (!time.isAfter(fimDia)) {
            horarios.add(time.toString());
            time = time.plusMinutes(30);
        }

        // Tarde: 14:00 at├® 17:00
        //time = LocalTime.of(14, 0);
        //LocalTime fimTarde = LocalTime.of(18, 0);

//        while (!time.isAfter(fimTarde)) {
//            horarios.add(time.toString());
//            time = time.plusMinutes(30);
//        }

        return horarios;
    }


    private void configurarColunasELinhas(int totalRows) {

        agendaGrid.getRowConstraints().clear();
        agendaGrid.getColumnConstraints().clear();

        // ===== COLUNA DE HORÁRIO =====
        ColumnConstraints colHora = new ColumnConstraints();
        colHora.setPrefWidth(80);
        colHora.setMinWidth(80);
        agendaGrid.getColumnConstraints().add(colHora);

        // ===== COLUNAS DOS DIAS =====
        for (int i = 1; i <= 6; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setMinWidth(100);
            agendaGrid.getColumnConstraints().add(col);
        }

        // ===== LINHA DO CABEÇALHO =====
        RowConstraints header = new RowConstraints();
        header.setMinHeight(40);
        header.setPrefHeight(40);
        header.setMaxHeight(40);
        header.setVgrow(Priority.NEVER);
        agendaGrid.getRowConstraints().add(header);

        // ===== DEMAIS LINHAS DOS HORÁRIOS =====
        for (int i = 1; i < totalRows; i++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(80);
            row.setPrefHeight(80);
            row.setVgrow(Priority.NEVER);
            agendaGrid.getRowConstraints().add(row);
        }
    }


    private LocalDate semanaInicio;
    private LocalDate semanaFim;

    private void carregarAgendamentos() {

        List<Agendamento> agendamentos = agendamentoRepository.findByDataBetween(
                semanaInicio,
                semanaFim
        );

        for (Agendamento ag : agendamentos) {
            renderizarAgendamento(ag);
        }

    }

    private int calcularColuna(Agendamento ag) {
        // Segunda = 1 ... Domingo = 7
        return ag.getData().getDayOfWeek().getValue();
    }

    private int calcularLinha(LocalTime hora) {

        LocalTime inicio = LocalTime.of(8, 0);

        int minutos = (hora.getHour() * 60 + hora.getMinute() - inicio.getHour() * 60);

        return (minutos / 30) + 1;
    }

    private int calcularRowSpan(Agendamento ag) {

        int minutos = ag.getProcedimento()
                .getTempo_previsto()
                .toLocalTime()
                .getHour() * 60
                + ag.getProcedimento()
                .getTempo_previsto()
                .toLocalTime()
                .getMinute();

        return (int) Math.ceil(minutos / 30.0);
    }

    private Pane criarBloco(Agendamento ag) {

        VBox box = new VBox(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER);

        box.setMaxWidth(Double.MAX_VALUE);
        box.setMaxHeight(Double.MAX_VALUE);

        box.setStyle("""
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: black;
        -fx-border-width: 0.5;  
        -fx-background-color:
        """ + corPorProcedimento(ag) + ";");

        LocalTime fim = calcularHoraFim(ag);

        Label horario = new Label(
                ag.getHora() + " - " + fim
        );

        Label nome = new Label(
                ag.getPaciente().getNome()
        );
        nome.setStyle("-fx-font-weight: bold;");

        Label proc = new Label(
                ag.getProcedimento().getNome()
        );

        box.getChildren().addAll(horario, nome, proc);

        return box;

    }


    private LocalTime calcularHoraFim(Agendamento ag) {

        int minutos =
                ag.getProcedimento()
                        .getTempo_previsto()
                        .toLocalTime()
                        .getHour() * 60
                        + ag.getProcedimento()
                        .getTempo_previsto()
                        .toLocalTime()
                        .getMinute();

        return ag.getHora().plusMinutes(minutos);

    }

    private void renderizarAgendamento(Agendamento ag) {
        int coluna = calcularColuna(ag);
        int linha = calcularLinha(ag.getHora());
        int rowSpan = calcularRowSpan(ag);

        Pane bloco = criarBloco(ag);

        agendaGrid.add(bloco, coluna, linha,1, rowSpan);
    }

    private String corPorProcedimento(Agendamento ag) {
        return ag.getProcedimento().getCorHex();
    }

    private void atualizarSemana(LocalDate dataSelecionada) {

        // Calcula a segunda-feira da semana selecionada
        semanaInicio = dataSelecionada.with(DayOfWeek.MONDAY);
        semanaFim = semanaInicio.plusDays(6);

        // Limpa e recria a agenda
        montarAgenda();
        renderizarAlmoco();
        carregarAgendamentos();
    }

    private boolean isHorarioAlmoco(LocalTime horario) {
        return !horario.isBefore(ALMOCO_INICIO) && horario.isBefore(ALMOCO_FIM);
    }

    private StackPane criarBlocoAlmoco() {

        Label label = new Label("Horário de almoço");
        label.setStyle("""
        -fx-text-fill: #6c757d;
        -fx-font-weight: bold;
        """);

        StackPane bloco = new StackPane(label);
        bloco.setPrefSize(150, 50);

        bloco.setStyle("""
        -fx-background-color: #c3c3c3;
        -fx-background-radius: 2;
        -fx-border-radius: 2;
        """);

        return bloco;
    }

    private void renderizarAlmoco() {

        // cada linha = 30 minutos
        LocalTime inicioAgenda = LocalTime.of(8, 0);

        int linhaInicio = calcularLinha(ALMOCO_INICIO); // 12:00
        int rowSpan = 4; // 12:00 até 14:00 (4 blocos de 30 min)

        for (int col = 1; col <= 6; col++) {

            StackPane blocoAlmoco = criarBlocoAlmoco();

            // impede interação
            blocoAlmoco.setDisable(true);

            agendaGrid.add(
                    blocoAlmoco,
                    col,
                    linhaInicio,
                    1,
                    rowSpan
            );
        }
    }


}
