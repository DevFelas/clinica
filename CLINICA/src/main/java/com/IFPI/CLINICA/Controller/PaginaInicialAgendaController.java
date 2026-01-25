package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Agendamento;
import com.IFPI.CLINICA.Repository.AgendamentoRepository;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class PaginaInicialAgendaController implements Initializable {

    @FXML
    private GridPane agendaGrid;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private Navigator navigator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        semanaInicio = LocalDate.now().with(DayOfWeek.MONDAY); // Pega a segunda-feira da semana atual
        semanaFim = semanaInicio.plusDays(6); //incrementa 6 dias

        montarAgenda();
        carregarAgendamentos();
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
//    @FXML
//    private void irParaFinaneiro(ActionEvent event) {
//        navigator.trocarPagina(
//                (Node) event.getSource(),
//                "/view/pages/Financeiro.fxml"
//        );
//    }

    // BOTÕES DA LATERAL ESQUERDA

    // Botão para ir para tela novo agendamento//@FXML
//    private void irParaNovoAgendamento(ActionEvent event) {
//        navigator.trocarPagina(
//                (Node) event.getSource(),
//                "/view/pages/CadasPessoa.fxml"
//        );
//    }

    // Botão para ir para tela Editar
//    @FXML
//    private void irParaEditar(ActionEvent event) {
//        navigator.trocarPagina(
//                (Node) event.getSource(),
//                "/view/pages/CadasPessoa.fxml"
//        );
//    }

    // Botão para ir para tela Cancelar
    @FXML
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

        String[] dias = {"08:00", "Segunda\n 19/01", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};

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

            horaLabel.setMinHeight(40);
            horaLabel.setPrefHeight(40);
            horaLabel.setMaxHeight(40);

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
                cell.setMinHeight(40);
                cell.setPrefHeight(40);

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

        // Tarde: 14:00 até 17:00
        //time = LocalTime.of(14, 0);
        //LocalTime fimTarde = LocalTime.of(18, 0);

//        while (!time.isAfter(fimTarde)) {
//            horarios.add(time.toString());
//            time = time.plusMinutes(30);
//        }

        return horarios;
    }


    private void configurarColunasELinhas(int totalRows) {


        ColumnConstraints colHora = new ColumnConstraints();
        colHora.setPrefWidth(40);
        colHora.setMinWidth(80);
        agendaGrid.getColumnConstraints().add(colHora);


        for (int i = 1; i <= 6; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setMinWidth(100);
            agendaGrid.getColumnConstraints().add(col);
        }


        for (int i = 0; i < totalRows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(40);
            row.setMinHeight(40);
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

}