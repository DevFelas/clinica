package com.IFPI.CLINICA.Controller;

import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class PaginaInicialAgendaController implements Initializable {

    @FXML
    private GridPane agendaGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        montarAgenda();
    }

    private void montarAgenda() {
        agendaGrid.getChildren().clear();
        agendaGrid.getColumnConstraints().clear();
        agendaGrid.getRowConstraints().clear();
        agendaGrid.setGridLinesVisible(false);

        String[] dias = {"", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};

        for (int col = 0; col < dias.length; col++) {
            Label label = new Label(dias[col]);
            label.setPrefHeight(40);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);

            label.setStyle("""
                -fx-background-color: #A5D6C1;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-border-color: #D6EAF8;
                -fx-border-width: 0 1 1 0;
            """);

            agendaGrid.add(label, col, 0);
        }


        List<String> horarios = gerarHorarios();

        int row = 1;

        for (String hora : horarios) {

            Label horaLabel = new Label(hora);
            horaLabel.setPrefWidth(80);
            horaLabel.setMaxWidth(Double.MAX_VALUE);
            horaLabel.setAlignment(Pos.CENTER_RIGHT);

            horaLabel.setStyle("""
                -fx-padding: 0 10 0 0;
                -fx-background-color: #F2F3F4;
                -fx-border-color: #D5D8DC;
                -fx-border-width: 0 1 1 0;
            """);

            agendaGrid.add(horaLabel, 0, row);

            for (int col = 1; col <= 6; col++) {
                Pane cell = new Pane();
                cell.setMinHeight(40);
                cell.setPrefHeight(40);

                cell.setStyle("""
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


        LocalTime time = LocalTime.of(8, 0);
        LocalTime fimManha = LocalTime.of(12, 0);

        while (!time.isAfter(fimManha)) {
            horarios.add(time.toString());
            time = time.plusMinutes(30);
        }

        // Tarde: 14:00 até 17:00
        time = LocalTime.of(14, 0);
        LocalTime fimTarde = LocalTime.of(17, 0);

        while (!time.isAfter(fimTarde)) {
            horarios.add(time.toString());
            time = time.plusMinutes(30);
        }

        return horarios;
    }


    private void configurarColunasELinhas(int totalRows) {


        ColumnConstraints colHora = new ColumnConstraints();
        colHora.setPrefWidth(80);
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
}