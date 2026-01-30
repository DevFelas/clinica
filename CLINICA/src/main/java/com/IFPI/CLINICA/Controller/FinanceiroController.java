package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Util.Navigator;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Service.clinicaTeste.FinanceiroService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class FinanceiroController implements Initializable {

    @Autowired
    private Navigator navigator;

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private ApplicationContext springContext;

    // Campos do resumo financeiro
    @FXML private TextField totalReceitasField;
    @FXML private TextField lucroField;
    @FXML private TextField totalDespesasField;

    // Campos dos procedimentos
    @FXML private TextField consultaField;
    @FXML private TextField limpezaField;
    @FXML private TextField exodontiaField;
    @FXML private TextField proteseField;
    @FXML private TextField implanteField;
    @FXML private TextField manutencaoField;
    @FXML private TextField montagemField;
    @FXML private TextField restauracaoField;

    // Campos de data
    @FXML private DatePicker dataInicioPicker;
    @FXML private DatePicker dataFimPicker;

    // Tabela
    @FXML private TableView<TransacaoFinanceira> tabelaFinanceiro;
    @FXML private TableColumn<TransacaoFinanceira, String> colData;
    @FXML private TableColumn<TransacaoFinanceira, String> colPaciente;
    @FXML private TableColumn<TransacaoFinanceira, String> colCategoria;
    @FXML private TableColumn<TransacaoFinanceira, String> colValor;
    @FXML private TableColumn<TransacaoFinanceira, String> colTipo;
    @FXML private TableColumn<TransacaoFinanceira, String> colStatus;
    @FXML private TableColumn<TransacaoFinanceira, Void> colAcoes;

    // Labels de informações
    @FXML private Label labelCargo;
    @FXML private Label infoNome;
    @FXML private Label infoCpf;
    @FXML private Label infoProcedimento;
    @FXML private Label infoDataProc;
    @FXML private Label infoValor;
    @FXML private Label infoStatus;

    private ObservableList<TransacaoFinanceira> transacoes = FXCollections.observableArrayList();
    private NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== FINANCEIRO CONTROLLER INITIALIZE ===");

        // Configurar cargo do usuário
        if (SessaoUsuario.getInstance().getUsuarioLogado() != null) {
            Usuario usuario = SessaoUsuario.getInstance().getUsuarioLogado();
            labelCargo.setText(usuario.getPerfil().toString());
            System.out.println("Usuário logado: " + usuario.getLogin() + " - Perfil: " + usuario.getPerfil());
        }

        // Configurar datas padrão (últimos 30 dias)
        dataFimPicker.setValue(LocalDate.now());
        dataInicioPicker.setValue(LocalDate.now().minusDays(30));
        System.out.println("Datas configuradas: " + dataInicioPicker.getValue() + " até " + dataFimPicker.getValue());

        // Configurar tabela
        configurarTabela();

        // Carregar dados iniciais
        carregarDados();

        // Configurar listeners para datas
        dataInicioPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Data início alterada para: " + newVal);
            carregarDados();
        });
        dataFimPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Data fim alterada para: " + newVal);
            carregarDados();
        });
    }

    private void configurarTabela() {
        System.out.println("=== CONFIGURANDO TABELA ===");

        // Configurar as colunas da tabela
        colData.setCellValueFactory(cellData -> {
            TransacaoFinanceira transacao = cellData.getValue();
            if (transacao != null && transacao.getData() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        transacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colPaciente.setCellValueFactory(cellData -> {
            TransacaoFinanceira transacao = cellData.getValue();
            if (transacao != null) {
                if (transacao.getPaciente() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                            transacao.getPaciente().getNome()
                    );
                } else {
                    return new javafx.beans.property.SimpleStringProperty(
                            transacao.getDescricao()
                    );
                }
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colCategoria.setCellValueFactory(cellData -> {
            TransacaoFinanceira transacao = cellData.getValue();
            if (transacao != null && transacao.getCategoria() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        transacao.getCategoria().getDescricao()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colValor.setCellValueFactory(cellData -> {
            TransacaoFinanceira transacao = cellData.getValue();
            if (transacao != null && transacao.getValor() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        formatoMoeda.format(transacao.getValor())
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colTipo.setCellValueFactory(cellData -> {
            TransacaoFinanceira transacao = cellData.getValue();
            if (transacao != null && transacao.getTipo() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        transacao.getTipo() == TipoTransacao.ENTRADA ? "Entrada" : "Saída"
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // IMPORTANTE: Configurar cellValueFactory para a coluna de status
        colStatus.setCellValueFactory(cellData -> {
            TransacaoFinanceira transacao = cellData.getValue();
            if (transacao != null && transacao.getStatus() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        transacao.getStatus().toString()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Configurar coluna de status com cores
        colStatus.setCellFactory(column -> new TableCell<TransacaoFinanceira, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    TransacaoFinanceira transacao = getTableView().getItems().get(getIndex());
                    if (transacao != null && transacao.getStatus() != null) {
                        StatusTransacao status = transacao.getStatus();
                        setText(status.toString());

                        switch (status) {
                            case PAGO:
                                setTextFill(Color.GREEN);
                                setStyle("-fx-font-weight: bold;");
                                break;
                            case PENDENTE:
                                setTextFill(Color.ORANGE);
                                setStyle("-fx-font-weight: bold;");
                                break;
                            case CANCELADO:
                                setTextFill(Color.RED);
                                setStyle("-fx-font-weight: bold;");
                                break;
                        }
                    }
                }
            }
        });

        // Configurar coluna de ações (botões Pagar/Cancelar)
        colAcoes.setCellFactory(param -> new TableCell<TransacaoFinanceira, Void>() {
            private final Button btnPagar = new Button("Pagar");
            private final Button btnCancelar = new Button("Cancelar");
            private final HBox pane = new HBox(5, btnPagar, btnCancelar);

            {
                btnPagar.setStyle("-fx-background-color: #36bc89; -fx-text-fill: white; -fx-padding: 5 10;");
                btnCancelar.setStyle("-fx-background-color: #d82123; -fx-text-fill: white; -fx-padding: 5 10;");

                btnPagar.setOnAction(event -> {
                    TransacaoFinanceira transacao = getTableView().getItems().get(getIndex());
                    if (transacao != null) {
                        confirmarPagamento(transacao);
                    }
                });

                btnCancelar.setOnAction(event -> {
                    TransacaoFinanceira transacao = getTableView().getItems().get(getIndex());
                    if (transacao != null) {
                        confirmarCancelamento(transacao);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransacaoFinanceira transacao = getTableView().getItems().get(getIndex());
                    if (transacao != null && transacao.getStatus() == StatusTransacao.PENDENTE) {
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Configurar listener para seleção na tabela
        tabelaFinanceiro.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        System.out.println("Transação selecionada: " + newValue.getId() + " - " + newValue.getDescricao());
                        atualizarDetalhesTransacao(newValue);
                    } else {
                        limparDetalhesTransacao();
                    }
                });

        System.out.println("Tabela configurada com sucesso");
    }

    private void carregarDados() {
        try {
            System.out.println("=== CARREGANDO DADOS FINANCEIROS ===");
            LocalDate inicio = dataInicioPicker.getValue();
            LocalDate fim = dataFimPicker.getValue();
            System.out.println("Período: " + inicio + " até " + fim);

            // Carregar transações do service
            System.out.println("Chamando financeiroService.listarTransacoes...");
            transacoes.setAll(financeiroService.listarTransacoes(inicio, fim));
            System.out.println("Transações carregadas na ObservableList: " + transacoes.size());

            tabelaFinanceiro.setItems(transacoes);
            System.out.println("TableView items: " + tabelaFinanceiro.getItems().size());

            // Calcular totais
            System.out.println("Calculando totais...");
            BigDecimal totalEntradas = financeiroService.calcularTotalEntradas(inicio, fim);
            BigDecimal totalSaidas = financeiroService.calcularTotalSaidas(inicio, fim);
            BigDecimal lucro = financeiroService.calcularLucro(inicio, fim);

            System.out.println("Total Entradas: " + totalEntradas);
            System.out.println("Total Saídas: " + totalSaidas);
            System.out.println("Lucro: " + lucro);

            // Atualizar campos
            totalReceitasField.setText(formatoMoeda.format(totalEntradas));
            totalDespesasField.setText(formatoMoeda.format(totalSaidas));
            lucroField.setText(formatoMoeda.format(lucro));

            // Cor do lucro
            if (lucro.compareTo(BigDecimal.ZERO) < 0) {
                lucroField.setStyle("-fx-text-fill: #d82123; -fx-font-weight: bold; -fx-font-size: 18;");
            } else {
                lucroField.setStyle("-fx-text-fill: #00a859; -fx-font-weight: bold; -fx-font-size: 18;");
            }

            // Carregar valores dos procedimentos
            carregarValoresProcedimentos();

            // Forçar atualização da tabela
            tabelaFinanceiro.refresh();
            System.out.println("Tabela atualizada. Total de itens: " + tabelaFinanceiro.getItems().size());

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
            e.printStackTrace();

            // Mostrar alerta de erro para o usuário
            mostrarAlerta("Erro", "Não foi possível carregar os dados financeiros: " + e.getMessage());
        }
    }

    private void carregarValoresProcedimentos() {
        try {
            // Aqui você implementaria a lógica para buscar os valores dos procedimentos do banco
            // Por enquanto, valores fixos conforme a imagem
            consultaField.setText("R$ 100,00");
            limpezaField.setText("R$ 100,00");
            exodontiaField.setText("R$ 250,00");
            proteseField.setText("R$ 800,00");
            implanteField.setText("R$ 1.500,00");
            manutencaoField.setText("R$ 80,00");
            montagemField.setText("R$ 500,00");
            restauracaoField.setText("R$ 150,00");

        } catch (Exception e) {
            System.err.println("Erro ao carregar valores dos procedimentos: " + e.getMessage());
        }
    }

    private void atualizarDetalhesTransacao(TransacaoFinanceira transacao) {
        if (transacao != null) {
            if (transacao.getPaciente() != null) {
                infoNome.setText(transacao.getPaciente().getNome());
                infoCpf.setText(transacao.getPaciente().getCpf());
            } else {
                infoNome.setText("N/A");
                infoCpf.setText("N/A");
            }

            if (transacao.getProcedimento() != null) {
                infoProcedimento.setText(transacao.getProcedimento().getNome());
            } else if (transacao.getCategoria() != null) {
                infoProcedimento.setText(transacao.getCategoria().getDescricao());
            } else {
                infoProcedimento.setText("N/A");
            }

            if (transacao.getData() != null) {
                infoDataProc.setText(transacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                infoDataProc.setText("N/A");
            }

            if (transacao.getValor() != null) {
                infoValor.setText(formatoMoeda.format(transacao.getValor()));
            } else {
                infoValor.setText("N/A");
            }

            if (transacao.getStatus() != null) {
                infoStatus.setText(transacao.getStatus().toString());

                switch (transacao.getStatus()) {
                    case PAGO:
                        infoStatus.setTextFill(Color.GREEN);
                        break;
                    case PENDENTE:
                        infoStatus.setTextFill(Color.ORANGE);
                        break;
                    case CANCELADO:
                        infoStatus.setTextFill(Color.RED);
                        break;
                }
            }
        }
    }

    private void limparDetalhesTransacao() {
        infoNome.setText("-");
        infoCpf.setText("-");
        infoProcedimento.setText("-");
        infoDataProc.setText("-");
        infoValor.setText("-");
        infoStatus.setText("-");
        infoStatus.setTextFill(Color.BLACK);
    }

    // ==================== MÉTODOS DE NAVEGAÇÃO ====================
    @FXML
    private void irParaAgenda(ActionEvent event) {
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/Agenda.fxml");
    }

    @FXML
    private void irParaPacientes(ActionEvent event) {
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/TodosPacientes.fxml");
    }

    @FXML
    private void irParaRegistro(ActionEvent event) {
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/Registro.fxml");
    }

    @FXML
    private void irParaFinanceiro(ActionEvent event) {
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/Financeiro.fxml");
    }

    // ==================== MÉTODOS DOS BOTÕES DIREITOS ====================
    @FXML
    private void onNovaReceita() {
        abrirDialogoReceita();
    }

    @FXML
    private void onNovaDespesa() {
        abrirDialogoDespesa();
    }

    @FXML
    private void onNovoProcedimento() {
        mostrarAlerta("Novo Procedimento", "Funcionalidade em desenvolvimento!\n\nEsta funcionalidade permitirá cadastrar novos procedimentos odontológicos com seus respectivos valores.");
    }

    @FXML
    private void onRelatorioMensal() {
        try {
            LocalDate inicio = dataInicioPicker.getValue();
            LocalDate fim = dataFimPicker.getValue();

            BigDecimal totalEntradas = financeiroService.calcularTotalEntradas(inicio, fim);
            BigDecimal totalSaidas = financeiroService.calcularTotalSaidas(inicio, fim);
            BigDecimal lucro = totalEntradas.subtract(totalSaidas);

            String relatorio = String.format(
                    "=== RELATÓRIO FINANCEIRO ===\n\n" +
                            "Período: %s até %s\n\n" +
                            "Total de Entradas: %s\n" +
                            "Total de Saídas: %s\n" +
                            "Lucro: %s\n\n" +
                            "Total de Transações: %d",
                    inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    formatoMoeda.format(totalEntradas),
                    formatoMoeda.format(totalSaidas),
                    formatoMoeda.format(lucro),
                    transacoes.size()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Relatório Mensal");
            alert.setHeaderText("Relatório Gerado");
            alert.setContentText(relatorio);
            alert.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @FXML
    private void onExportarPDF() {
        mostrarAlerta("Exportar PDF", "Funcionalidade em development!\n\nEsta funcionalidade permitirá exportar relatórios financeiros em formato PDF para impressão ou arquivamento.");
    }

    @FXML
    private void onSair() {
        SessaoUsuario.getInstance().limparSessao();
        navigator.trocarPagina((Node) infoNome, "/view/pages/Login.fxml");
    }

    @FXML
    private void onSalvarAlteracoes() {
        try {
            // Aqui você implementaria a lógica para salvar os valores dos procedimentos
            // Por enquanto, apenas mostra uma mensagem
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Valores dos procedimentos atualizados com sucesso!");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao atualizar valores");
            alert.setContentText("Verifique os valores inseridos: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================
    private void confirmarPagamento(TransacaoFinanceira transacao) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Pagamento");
        alert.setHeaderText("Deseja marcar esta transação como PAGA?");
        alert.setContentText("Transação: " + transacao.getDescricao() +
                "\nValor: " + formatoMoeda.format(transacao.getValor()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                TransacaoFinanceira atualizada = financeiroService.atualizarStatus(
                        transacao.getId(), StatusTransacao.PAGO
                );
                if (atualizada != null) {
                    mostrarAlerta("Sucesso", "Transação marcada como PAGA!");
                    carregarDados();
                }
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro ao atualizar status: " + e.getMessage());
            }
        }
    }

    private void confirmarCancelamento(TransacaoFinanceira transacao) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cancelamento");
        alert.setHeaderText("Deseja CANCELAR esta transação?");
        alert.setContentText("ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
                "Transação: " + transacao.getDescricao() +
                "\nValor: " + formatoMoeda.format(transacao.getValor()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                TransacaoFinanceira atualizada = financeiroService.atualizarStatus(
                        transacao.getId(), StatusTransacao.CANCELADO
                );
                if (atualizada != null) {
                    mostrarAlerta("Sucesso", "Transação CANCELADA!");
                    carregarDados();
                }
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro ao cancelar transação: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void abrirDialogoReceita() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/pages/Dialogs/NovaReceitaDialog.fxml")
            );
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nova Receita");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(infoNome.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Quando o diálogo for fechado, atualizar os dados
            dialogStage.setOnHidden(event -> {
                carregarDados();
            });

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o diálogo de nova receita: " + e.getMessage());
        }
    }

    private void abrirDialogoDespesa() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/pages/Dialogs/NovaDespesaDialog.fxml")
            );
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nova Despesa");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(infoNome.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Quando o diálogo for fechado, atualizar os dados
            dialogStage.setOnHidden(event -> {
                carregarDados();
            });

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o diálogo de nova despesa: " + e.getMessage());
        }
    }

    // Método de teste para diagnóstico
    @FXML
    private void testarCarregamento() {
        System.out.println("=== TESTANDO CARREGAMENTO MANUAL ===");
        carregarDados();
    }
}