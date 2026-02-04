package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Controller.SuperController;
import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Service.AgendamentoService;
import com.IFPI.CLINICA.Service.ProcedimentoService;
import com.IFPI.CLINICA.Util.Navigator;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.event.ActionEvent;

import java.io.IOException;
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

/**
 * Controller principal da tela de Agenda.
 * Gerencia a visualização semanal dos agendamentos em um GridPane dinâmico,
 * tratando regras de perfil de acesso, renderização de blocos de horário,
 * intervalos de almoço e navegação do sistema.
 */
@Component
public class PagAgendaController extends SuperController implements Initializable {

    // ALTERAÇÃO PARA SERVICE AQUI:
    @Autowired private AgendamentoService agendamentoService;
    @Autowired private ProcedimentoService procedimentoService;
    @Autowired private ConfigurableApplicationContext springContext;
    @Autowired private Navigator navigator;

    @FXML private GridPane agendaGrid;
    @FXML private Button btnFinanceiro;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    @FXML private Button btnDetalhar;
    @FXML private Label textUsuario;
    @FXML private DatePicker datePicker;
    @FXML private VBox boxProcedimentos;

    private Agendamento agendamentoSelecionado;
    private Pane blocoSelecionado;

    private static final LocalTime ALMOCO_INICIO = LocalTime.of(12, 0);
    private static final LocalTime ALMOCO_FIM = LocalTime.of(14, 0);

    /**
     * Inicializa a tela configurando as permissões do usuário logado,
     * calculando a semana atual e disparando a renderização visual da agenda.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        aplicarPermissoesUI(btnFinanceiro, textUsuario);

        semanaInicio = LocalDate.now().with(DayOfWeek.MONDAY); // Pega a segunda-feira da semana atual
        semanaFim = semanaInicio.plusDays(6); //incrementa 6 dias

        montarAgenda();
        renderizarAlmoco();
        carregarAgendamentos();
        carregarLegendaProcedimentos();

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                atualizarSemana(newDate);
            }
        });
        // desabilita botões até selecionar um agendamento
        btnEditar.setDisable(true);
        btnCancelar.setDisable(true);
        btnDetalhar.setDisable(true);
    }


    // +========================+ //
    // BOTÕES DA LATERAL ESQUERDA //
    // +========================+ //

    /**
     * Abre o modal de edição para o agendamento selecionado.
     */
    @FXML
    private void irParaEditar(ActionEvent event) {
        if (agendamentoSelecionado == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/DetalharAgendamento.fxml"));
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            ModalDetalhesController controller = loader.getController();
            controller.configurar(agendamentoSelecionado, ModoTelaAgendamento.EDICAO);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalhes do Agendamento");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.showAndWait();

            // ATUALIZA SÓ SE ALTEROU
            if (controller.isAlterou()) { atualizarAgenda(); }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre o modal em modo de visualização (detalhes) para o agendamento selecionado.
     */
    @FXML
    private void irParaDetalhar(ActionEvent event) {
        if (agendamentoSelecionado == null) return;
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/view/pages/DetalharAgendamento.fxml") );
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            ModalDetalhesController controller = loader.getController();
            controller.configurar( agendamentoSelecionado, ModoTelaAgendamento.DETALHE );

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalhes do Agendamento");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.showAndWait();

            if (controller.isAlterou()) { atualizarAgenda(); }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Solicita confirmação e cancela o agendamento selecionado através do service.
     */
    @FXML
    private void cancelarAgendamento() {
        if (agendamentoSelecionado == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar Agendamento");
        confirm.setHeaderText(null);
        confirm.setContentText("Tem certeza que deseja cancelar este agendamento?");

        confirm.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                // ALTERAÇÃO PARA SERVICE AQUI:
                try {
                    agendamentoService.cancelarAgendamento(agendamentoSelecionado.getId());
                } catch (RuntimeException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                }
                atualizarAgenda();
            }
        });
    }

    // +======================+ //
    // COMPONENTES VISUAIS / UI //
    // +======================+ //

    /**
     * Constrói a estrutura visual da agenda (linhas de tempo e colunas de dias).
     */
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

    /**
     * Limpa a seleção atual e reconstrói todos os elementos da agenda.
     */
    private void atualizarAgenda() {

        montarAgenda();
        renderizarAlmoco();
        carregarAgendamentos();

        agendamentoSelecionado = null;
        blocoSelecionado = null;

        btnEditar.setDisable(true);
        btnCancelar.setDisable(true);
        btnDetalhar.setDisable(true);
    }

    /**
     * Gera os intervalos de tempo (cada 30 min) exibidos no eixo vertical da agenda.
     */
    private List<String> gerarHorarios() {
        List<String> horarios = new ArrayList<>();


        LocalTime time = LocalTime.of(8, 30);
        LocalTime fimDia = LocalTime.of(18, 0);

        while (!time.isAfter(fimDia)) {
            horarios.add(time.toString());
            time = time.plusMinutes(30);
        }
        return horarios;
    }

    /**
     * Define as restrições de crescimento e tamanho das colunas e linhas do GridPane.
     */
    private void configurarColunasELinhas(int totalRows) {

        agendaGrid.getRowConstraints().clear();
        agendaGrid.getColumnConstraints().clear();

        // ===== COLUNA DE HORARIO =====
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

        // ===== DEMAIS LINHAS DOS HORARIOS =====
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

    /**
     * Busca os agendamentos no banco de dados para o período semanal visível e os renderiza.
     */
    private void carregarAgendamentos() {

        agendaGrid.getChildren()
                .removeIf(node -> "AGENDAMENTO".equals(node.getUserData()));

        List<Agendamento> agendamentos =
                agendamentoService.listarPorPeriodo(semanaInicio, semanaFim);

        for (Agendamento ag : agendamentos) {

            if (ag.getStatus() == StatusAgendamento.CANCELADA) {
                continue;
            }

            renderizarAgendamento(ag);
        }
    }

    private int calcularColuna(Agendamento ag) {
        // Segunda = 1 ... Domingo = 7
        return ag.getData().getDayOfWeek().getValue();
    }

    /**
     * Calcula o índice da linha baseado na hora de início do agendamento.
     */
    private int calcularLinha(LocalTime hora) {

        LocalTime inicio = LocalTime.of(8, 0);

        int minutos = (hora.getHour() * 60 + hora.getMinute() - inicio.getHour() * 60);

        return (minutos / 30) + 1;
    }

    /**
     * Determina quantas linhas o bloco deve ocupar baseado na duração do procedimento.
     */
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

    /**
     * Cria o componente visual (Pane) que representa o agendamento no grid.
     */
    private Pane criarBloco(Agendamento ag) {

        // Conteúdo principal
        VBox box = new VBox(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setMaxHeight(Double.MAX_VALUE);

        String corBase = corPorProcedimento(ag);

        // escurece se REALIZADA
        String corFinal = ag.getStatus() == StatusAgendamento.REALIZADA
                ? escurecerCor(corBase, 0.75)
                : corBase;

        box.setStyle("""
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: black;
        -fx-border-width: 0.5;
        -fx-background-color: %s;
    """.formatted(corFinal));

        box.setUserData("AGENDAMENTO");

        LocalTime fim = calcularHoraFim(ag);

        Label horario = new Label(ag.getHora() + " - " + fim);
        Label nome = new Label(ag.getPaciente().getNome());
        nome.setStyle("-fx-font-weight: bold;");
        Label proc = new Label(ag.getProcedimento().getNome());

        box.getChildren().addAll(horario, nome, proc);

        // Icone de check (só se REALIZADA)
        Label check = new Label("✔");
        check.setStyle("""
        -fx-text-fill: #2e7d32;
        -fx-font-size: 14px;
        -fx-font-weight: bold;
    """);
        check.setVisible(ag.getStatus() == StatusAgendamento.REALIZADA);

        StackPane.setAlignment(check, Pos.TOP_RIGHT);
        StackPane.setMargin(check, new Insets(4));

        StackPane container = new StackPane(box, check);

        // só permite seleção se NÃO for realizada
        if (ag.getStatus() == StatusAgendamento.AGENDADA) {
            container.setOnMouseClicked(e -> selecionarAgendamento(ag, box));
        } else {
            container.setDisable(true);
        }

        return container;
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

    /**
     * Posiciona o bloco visual do agendamento no grid seguindo coluna, linha e rowSpan.
     */
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

    /**
     * Atualiza o intervalo da semana exibida com base em uma nova data selecionada no DatePicker.
     */
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

    /**
     * Cria o bloco cinza indicativo do horário de almoço.
     */
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

    /**
     * Renderiza os blocos de almoço para todos os dias da semana no intervalo definido.
     */
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

    /**
     * Destaca visualmente o agendamento clicado e habilita as ações de controle (Editar/Cancelar/Detalhar).
     */
    private void selecionarAgendamento(Agendamento ag, Pane box) {

        // remove destaque do anterior
        if (blocoSelecionado != null) {
            blocoSelecionado.setStyle(
                    blocoSelecionado.getStyle()
                            .replace("-fx-border-color: white;", "-fx-border-color: black;")
                            .replace("-fx-border-width: 2;", "-fx-border-width: 0.5;")

            );
        }

        // destaca o novo
        box.setStyle(box.getStyle() + """
        -fx-border-width: 2;
        -fx-border-color: #000000;
    """);

        agendamentoSelecionado = ag;
        blocoSelecionado = box;

        // habilita botões
        btnEditar.setDisable(false);
        btnCancelar.setDisable(false);
        btnDetalhar.setDisable(false);
    }

    /**
     * Utilitário para escurecer cores hexadecimais (usado para sinalizar agendamentos realizados).
     */
    private String escurecerCor(String hex, double fator) {

        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);

        r = (int) (r * fator);
        g = (int) (g * fator);
        b = (int) (b * fator);

        return String.format("#%02x%02x%02x", r, g, b);
    }

    /**
     * Carrega a legenda de cores dos procedimentos na barra lateral.
     */
    private void carregarLegendaProcedimentos() {

        boxProcedimentos.getChildren().clear();

        List<Procedimento> procedimentos = procedimentoService.listarProcedimentos();

        for (Procedimento proc : procedimentos) {

            Circle cor = new Circle(6);
            cor.setFill(javafx.scene.paint.Color.web(proc.getCorHex()));

            Label nome = new Label(proc.getNome());

            HBox item = new HBox(6, cor, nome);
            item.setAlignment(Pos.CENTER_LEFT);

            boxProcedimentos.getChildren().add(item);
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
