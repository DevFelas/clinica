package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Util.Navigator;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Service.clinicaTeste.FinanceiroService;
import com.IFPI.CLINICA.Service.ProcedimentoService;
import javafx.application.Platform;
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
import javafx.scene.layout.GridPane;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FinanceiroController implements Initializable {

    @Autowired
    private Navigator navigator;

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private ProcedimentoService procedimentoService;

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

    // Labels de informações da transação selecionada
    @FXML private Label infoNome;
    @FXML private Label infoCpf;
    @FXML private Label infoProcedimento;
    @FXML private Label infoDataProc;
    @FXML private Label infoValor;
    @FXML private Label infoStatus;

    // Label de informação do usuário
    @FXML private Label textUsuario;

    private ObservableList<TransacaoFinanceira> transacoes = FXCollections.observableArrayList();
    private NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Mapa para armazenar os procedimentos por nome
    private Map<String, Procedimento> procedimentosMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("=== FINANCEIRO CONTROLLER INITIALIZE ===");
            System.out.println("Spring Context disponível: " + (springContext != null ? "SIM" : "NÃO"));
            System.out.println("FinanceiroService disponível: " + (financeiroService != null ? "SIM" : "NÃO"));
            System.out.println("ProcedimentoService disponível: " + (procedimentoService != null ? "SIM" : "NÃO"));

            // Recupera o usuário da sessão
            Usuario usuario = SessaoUsuario.getInstance().getUsuarioLogado();
            System.out.println("Usuário logado: " + (usuario != null ? usuario.getLogin() : "NULO"));

            // Configurar cargo do usuário (sem alterar lógica de senha)
            if (usuario != null) {
                if (usuario.getPerfil() == Perfil.RECEPCIONISTA) {
                    textUsuario.setText("RECEPCIONISTA");
                } else if (usuario.getPerfil() == Perfil.ADMIN) {
                    textUsuario.setText("ADMINISTRADOR");
                }
            }

            // Configurar datas padrão (últimos 30 dias)
            dataFimPicker.setValue(LocalDate.now());
            dataInicioPicker.setValue(LocalDate.now().minusDays(30));

            // Configurar tabela
            configurarTabela();

            // Carregar dados iniciais
            carregarDados();

            // Configurar listeners para datas
            dataInicioPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                carregarDados();
            });
            dataFimPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                carregarDados();
            });

            // Carregar procedimentos do banco
            carregarProcedimentosDoBanco();

            System.out.println("=== FINANCEIRO CONTROLLER INICIALIZADO COM SUCESSO ===");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO no initialize do FinanceiroController: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro de Inicialização", "Não foi possível inicializar a tela financeira: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        try {
            System.out.println("Configurando tabela financeira...");

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

            // Configurar cellValueFactory para a coluna de status
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
                            atualizarDetalhesTransacao(newValue);
                        } else {
                            limparDetalhesTransacao();
                        }
                    });

            System.out.println("Tabela configurada com sucesso");

        } catch (Exception e) {
            System.err.println("ERRO ao configurar tabela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarDados() {
        try {
            System.out.println("=== CARREGANDO DADOS FINANCEIROS ===");

            LocalDate inicio = dataInicioPicker.getValue();
            LocalDate fim = dataFimPicker.getValue();

            if (inicio == null || fim == null) {
                System.out.println("Datas não selecionadas, usando padrão");
                inicio = LocalDate.now().minusDays(30);
                fim = LocalDate.now();
                dataInicioPicker.setValue(inicio);
                dataFimPicker.setValue(fim);
            }

            System.out.println("Período: " + inicio + " até " + fim);

            // Carregar transações do service
            System.out.println("Buscando transações...");
            List<TransacaoFinanceira> transacoesList = financeiroService.listarTransacoes(inicio, fim);
            System.out.println("Encontradas " + transacoesList.size() + " transações");

            transacoes.setAll(transacoesList);
            tabelaFinanceiro.setItems(transacoes);

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

            System.out.println("=== DADOS CARREGADOS COM SUCESSO ===");

        } catch (Exception e) {
            System.err.println("ERRO ao carregar dados financeiros: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível carregar os dados financeiros: " + e.getMessage());
        }
    }

    private void carregarProcedimentosDoBanco() {
        try {
            System.out.println("=== CARREGANDO PROCEDIMENTOS DO BANCO ===");
            List<Procedimento> procedimentos = procedimentoService.listarProcedimentos();
            procedimentosMap.clear();

            for (Procedimento proc : procedimentos) {
                procedimentosMap.put(proc.getNome().toLowerCase(), proc);
                System.out.println("Procedimento: " + proc.getNome() + " - R$ " + proc.getValor());
            }

            System.out.println("Carregados " + procedimentosMap.size() + " procedimentos do banco");

        } catch (Exception e) {
            System.err.println("ERRO ao carregar procedimentos do banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarValoresProcedimentos() {
        try {
            System.out.println("=== CARREGANDO VALORES DOS PROCEDIMENTOS ===");

            // Buscar valores do banco para cada procedimento
            List<Procedimento> procedimentos = procedimentoService.listarProcedimentos();
            System.out.println("Total de procedimentos: " + procedimentos.size());

            for (Procedimento proc : procedimentos) {
                String nomeLower = proc.getNome().toLowerCase();
                BigDecimal valor = proc.getValor();
                String valorFormatado = formatoMoeda.format(valor);

                System.out.println("Processando: " + proc.getNome() + " (lowercase: " + nomeLower + ")");

                // Mapear para os campos corretos
                if (nomeLower.contains("consulta")) {
                    consultaField.setText(valorFormatado);
                    System.out.println("  -> Campo: Consulta = " + valorFormatado);
                } else if (nomeLower.contains("limpeza")) {
                    limpezaField.setText(valorFormatado);
                    System.out.println("  -> Campo: Limpeza = " + valorFormatado);
                } else if (nomeLower.contains("exodontia")) {
                    exodontiaField.setText(valorFormatado);
                    System.out.println("  -> Campo: Exodontia = " + valorFormatado);
                } else if (nomeLower.contains("prótese") || nomeLower.contains("protese")) {
                    proteseField.setText(valorFormatado);
                    System.out.println("  -> Campo: Prótese = " + valorFormatado);
                } else if (nomeLower.contains("implante")) {
                    implanteField.setText(valorFormatado);
                    System.out.println("  -> Campo: Implante = " + valorFormatado);
                } else if (nomeLower.contains("manutenção") || nomeLower.contains("manutencao")) {
                    manutencaoField.setText(valorFormatado);
                    System.out.println("  -> Campo: Manutenção = " + valorFormatado);
                } else if (nomeLower.contains("montagem")) {
                    montagemField.setText(valorFormatado);
                    System.out.println("  -> Campo: Montagem = " + valorFormatado);
                } else if (nomeLower.contains("restauração") || nomeLower.contains("restauracao")) {
                    restauracaoField.setText(valorFormatado);
                    System.out.println("  -> Campo: Restauração = " + valorFormatado);
                } else {
                    System.out.println("  -> Não mapeado: " + proc.getNome());
                }
            }

        } catch (Exception e) {
            System.err.println("ERRO ao carregar valores dos procedimentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== MÉTODOS PARA DETALHES DA TRANSAÇÃO ==========
    private void atualizarDetalhesTransacao(TransacaoFinanceira transacao) {
        try {
            if (transacao != null) {
                System.out.println("Atualizando detalhes da transação ID: " + transacao.getId());

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
                    infoProcedimento.setText(transacao.getDescricao());
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
                } else {
                    infoStatus.setText("N/A");
                    infoStatus.setTextFill(Color.BLACK);
                }
            }
        } catch (Exception e) {
            System.err.println("ERRO ao atualizar detalhes da transação: " + e.getMessage());
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
        System.out.println("Navegando para Agenda...");
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/Agenda.fxml");
    }

    @FXML
    private void irParaPacientes(ActionEvent event) {
        System.out.println("Navegando para Pacientes...");
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/TodosPacientes.fxml");
    }

    @FXML
    private void irParaRegistro(ActionEvent event) {
        System.out.println("Navegando para Registro...");
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/Registro.fxml");
    }

    @FXML
    private void irParaFinanceiro(ActionEvent event) {
        System.out.println("Navegando para Financeiro...");
        navigator.trocarPagina((Node) event.getSource(), "/view/pages/Financeiro.fxml");
    }

    // ==================== MÉTODOS DOS BOTÕES DIREITOS ====================
    @FXML
    private void onNovaReceita() {
        System.out.println("=== BOTÃO NOVA RECEITA CLICADO ===");
        System.out.println("Spring Context disponível: " + (springContext != null ? "SIM" : "NÃO"));
        abrirDialogoReceita();
    }

    @FXML
    private void onNovaDespesa() {
        System.out.println("=== BOTÃO NOVA DESPESA CLICADO ===");
        abrirDialogoDespesa();
    }

    @FXML
    private void onNovoProcedimento() {
        System.out.println("=== BOTÃO NOVO PROCEDIMENTO CLICADO ===");
        abrirDialogoNovoProcedimento();
    }

    @FXML
    private void onRelatorioMensal() {
        System.out.println("=== GERANDO RELATÓRIO MENSAL ===");
        try {
            LocalDate inicio = dataInicioPicker.getValue();
            LocalDate fim = dataFimPicker.getValue();

            if (inicio == null || fim == null) {
                mostrarAlerta("Erro", "Selecione um período válido para gerar o relatório.");
                return;
            }

            BigDecimal totalEntradas = financeiroService.calcularTotalEntradas(inicio, fim);
            BigDecimal totalSaidas = financeiroService.calcularTotalSaidas(inicio, fim);
            BigDecimal lucro = totalEntradas.subtract(totalSaidas);

            String relatorio = String.format(
                    "=== RELATÓRIO FINANCEIRO ===\n\n" +
                            "Período: %s até %s\n\n" +
                            "Total de Entradas: %s\n" +
                            "Total de Saídas: %s\n" +
                            "Lucro: %s\n\n" +
                            "Total de Transações: %d\n" +
                            "Transações Pendentes: %d\n" +
                            "Transações Pagas: %d\n" +
                            "Transações Canceladas: %d",
                    inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    formatoMoeda.format(totalEntradas),
                    formatoMoeda.format(totalSaidas),
                    formatoMoeda.format(lucro),
                    transacoes.size(),
                    transacoes.stream().filter(t -> t.getStatus() == StatusTransacao.PENDENTE).count(),
                    transacoes.stream().filter(t -> t.getStatus() == StatusTransacao.PAGO).count(),
                    transacoes.stream().filter(t -> t.getStatus() == StatusTransacao.CANCELADO).count()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Relatório Mensal");
            alert.setHeaderText("Relatório Gerado");
            alert.setContentText(relatorio);

            // Ajustar tamanho da janela do alerta
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(400, 300);

            alert.showAndWait();

            System.out.println("Relatório gerado com sucesso");

        } catch (Exception e) {
            System.err.println("ERRO ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @FXML
    private void onExportarPDF() {
        System.out.println("=== EXPORTANDO DADOS PARA PDF ===");
        try {
            // Criar um arquivo de texto temporário (simulação de exportação)
            LocalDate inicio = dataInicioPicker.getValue();
            LocalDate fim = dataFimPicker.getValue();

            if (inicio == null || fim == null) {
                mostrarAlerta("Erro", "Selecione um período válido para exportar.");
                return;
            }

            String conteudo = "RELATÓRIO FINANCEIRO - SGO\n";
            conteudo += "Período: " + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " até " + fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n";

            for (TransacaoFinanceira transacao : transacoes) {
                conteudo += String.format("%s | %s | %s | %s | %s\n",
                        transacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        transacao.getDescricao(),
                        formatoMoeda.format(transacao.getValor()),
                        transacao.getTipo(),
                        transacao.getStatus());
            }

            // Em um sistema real, aqui você usaria uma biblioteca como iText para gerar PDF
            // Por enquanto, vamos mostrar o conteúdo em um alerta
            TextArea textArea = new TextArea(conteudo);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(600, 400);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportação de Dados");
            alert.setHeaderText("Conteúdo para exportação (simulação)");
            alert.getDialogPane().setContent(scrollPane);
            alert.showAndWait();

            System.out.println("Exportação simulada com sucesso");

        } catch (Exception e) {
            System.err.println("ERRO ao exportar dados: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao exportar dados: " + e.getMessage());
        }
    }

    @FXML
    private void onSair() {
        System.out.println("=== SOLICITAÇÃO DE SAÍDA ===");
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Saída");
        confirm.setHeaderText("Deseja realmente sair do sistema?");
        confirm.setContentText("Você será redirecionado para a tela de login.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Saindo do sistema...");
            SessaoUsuario.getInstance().limparSessao();
            navigator.trocarPagina(textUsuario, "/view/pages/Login.fxml");
        }
    }

    @FXML
    private void onSalvarAlteracoes() {
        System.out.println("=== SALVANDO ALTERAÇÕES DOS PROCEDIMENTOS ===");
        try {
            // Mapear campos para nomes de procedimentos
            Map<String, TextField> camposProcedimento = new HashMap<>();
            camposProcedimento.put("Consulta", consultaField);
            camposProcedimento.put("Limpeza", limpezaField);
            camposProcedimento.put("Exodontia", exodontiaField);
            camposProcedimento.put("Prótese", proteseField);
            camposProcedimento.put("Implante", implanteField);
            camposProcedimento.put("Manutenção", manutencaoField);
            camposProcedimento.put("Montagem", montagemField);
            camposProcedimento.put("Restauração", restauracaoField);

            boolean algumAlterado = false;
            List<String> erros = new ArrayList<>();

            for (Map.Entry<String, TextField> entry : camposProcedimento.entrySet()) {
                String nomeProcedimento = entry.getKey();
                TextField campo = entry.getValue();
                String textoValor = campo.getText().trim();

                System.out.println("Processando campo: " + nomeProcedimento + " = " + textoValor);

                if (textoValor.isEmpty()) {
                    erros.add("Campo " + nomeProcedimento + " está vazio");
                    continue;
                }

                try {
                    // Remover "R$" e converter para BigDecimal
                    textoValor = textoValor.replace("R$", "").trim();
                    textoValor = textoValor.replace(".", "").replace(",", ".");
                    BigDecimal novoValor = new BigDecimal(textoValor);

                    // Buscar procedimento pelo nome
                    Procedimento procedimento = null;
                    for (Procedimento proc : procedimentosMap.values()) {
                        if (proc.getNome().equalsIgnoreCase(nomeProcedimento) ||
                                proc.getNome().toLowerCase().contains(nomeProcedimento.toLowerCase())) {
                            procedimento = proc;
                            break;
                        }
                    }

                    if (procedimento != null) {
                        // Verificar se o valor foi alterado
                        if (procedimento.getValor().compareTo(novoValor) != 0) {
                            System.out.println("Atualizando " + nomeProcedimento + " de " +
                                    procedimento.getValor() + " para " + novoValor);
                            procedimento.setValor(novoValor);
                            procedimentoService.atualizarProcedimentoPorId(procedimento.getId(), procedimento);
                            algumAlterado = true;
                            System.out.println("Atualizado " + nomeProcedimento + " para R$ " + novoValor);
                        } else {
                            System.out.println("Valor de " + nomeProcedimento + " não foi alterado");
                        }
                    } else {
                        String erroMsg = "Procedimento " + nomeProcedimento + " não encontrado no banco";
                        erros.add(erroMsg);
                        System.err.println(erroMsg);
                    }

                } catch (NumberFormatException e) {
                    String erroMsg = "Valor inválido para " + nomeProcedimento + ": " + campo.getText();
                    erros.add(erroMsg);
                    System.err.println(erroMsg);
                } catch (Exception e) {
                    String erroMsg = "Erro ao atualizar " + nomeProcedimento + ": " + e.getMessage();
                    erros.add(erroMsg);
                    System.err.println(erroMsg);
                    e.printStackTrace();
                }
            }

            if (!erros.isEmpty()) {
                Alert erroAlert = new Alert(Alert.AlertType.WARNING);
                erroAlert.setTitle("Erros de Validação");
                erroAlert.setHeaderText("Os seguintes erros foram encontrados:");
                erroAlert.setContentText(String.join("\n", erros));
                erroAlert.showAndWait();
            }

            if (algumAlterado) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Valores dos procedimentos atualizados com sucesso!");
                alert.showAndWait();

                // Recarregar dados
                carregarProcedimentosDoBanco();
                carregarValoresProcedimentos();
                System.out.println("Procedimentos atualizados e dados recarregados");
            } else if (erros.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informação");
                alert.setHeaderText(null);
                alert.setContentText("Nenhum valor foi alterado.");
                alert.showAndWait();
                System.out.println("Nenhum valor alterado");
            }

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao salvar alterações: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao atualizar valores");
            alert.setContentText("Verifique os valores inseridos: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================
    private void confirmarPagamento(TransacaoFinanceira transacao) {
        System.out.println("Confirmando pagamento da transação ID: " + transacao.getId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Pagamento");
        alert.setHeaderText("Deseja marcar esta transação como PAGA?");
        alert.setContentText("Transação: " + transacao.getDescricao() +
                "\nValor: " + formatoMoeda.format(transacao.getValor()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                System.out.println("Atualizando status para PAGO...");
                TransacaoFinanceira atualizada = financeiroService.atualizarStatus(
                        transacao.getId(), StatusTransacao.PAGO
                );
                if (atualizada != null) {
                    mostrarAlerta("Sucesso", "Transação marcada como PAGA!");
                    carregarDados();
                    if (tabelaFinanceiro.getSelectionModel().getSelectedItem() != null) {
                        atualizarDetalhesTransacao(atualizada);
                    }
                    System.out.println("Status atualizado com sucesso");
                }
            } catch (Exception e) {
                System.err.println("ERRO ao atualizar status: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao atualizar status: " + e.getMessage());
            }
        }
    }

    private void confirmarCancelamento(TransacaoFinanceira transacao) {
        System.out.println("Confirmando cancelamento da transação ID: " + transacao.getId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cancelamento");
        alert.setHeaderText("Deseja CANCELAR esta transação?");
        alert.setContentText("ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
                "Transação: " + transacao.getDescricao() +
                "\nValor: " + formatoMoeda.format(transacao.getValor()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                System.out.println("Atualizando status para CANCELADO...");
                TransacaoFinanceira atualizada = financeiroService.atualizarStatus(
                        transacao.getId(), StatusTransacao.CANCELADO
                );
                if (atualizada != null) {
                    mostrarAlerta("Sucesso", "Transação CANCELADA!");
                    carregarDados();
                    if (tabelaFinanceiro.getSelectionModel().getSelectedItem() != null) {
                        atualizarDetalhesTransacao(atualizada);
                    }
                    System.out.println("Transação cancelada com sucesso");
                }
            } catch (Exception e) {
                System.err.println("ERRO ao cancelar transação: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao cancelar transação: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
    }

    private void abrirDialogoReceita() {
        System.out.println("=== TENTANDO ABRIR DIALOGO DE NOVA RECEITA ===");
        try {
            System.out.println("1. Preparando FXMLLoader...");
            FXMLLoader loader = new FXMLLoader();

            // Tentar encontrar o arquivo FXML
            URL fxmlUrl = getClass().getResource("/view/pages/Dialogs/NovaReceitaDialog.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERRO: Arquivo FXML não encontrado: /view/pages/Dialogs/NovaReceitaDialog.fxml");

                // Tentar caminho alternativo
                fxmlUrl = getClass().getClassLoader().getResource("view/pages/Dialogs/NovaReceitaDialog.fxml");
                if (fxmlUrl == null) {
                    System.err.println("ERRO: Arquivo FXML também não encontrado no classloader");
                    mostrarAlerta("Erro", "Arquivo de diálogo não encontrado.\nCaminho: /view/pages/Dialogs/NovaReceitaDialog.fxml");
                    return;
                }
            }

            System.out.println("2. Arquivo FXML encontrado: " + fxmlUrl);
            loader.setLocation(fxmlUrl);

            System.out.println("3. Configurando Controller Factory...");
            if (springContext == null) {
                System.err.println("ERRO: Spring Context é nulo!");
                mostrarAlerta("Erro", "Spring Context não está disponível.");
                return;
            }

            loader.setControllerFactory(springContext::getBean);

            System.out.println("4. Carregando FXML...");
            Parent root;
            try {
                root = loader.load();
                System.out.println("5. FXML carregado com sucesso");
            } catch (Exception e) {
                System.err.println("ERRO ao carregar FXML: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Erro", "Falha ao carregar o arquivo de diálogo: " + e.getMessage());
                return;
            }

            System.out.println("6. Criando Stage...");
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nova Receita");
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // Obter a janela atual de forma segura
            if (textUsuario != null && textUsuario.getScene() != null && textUsuario.getScene().getWindow() != null) {
                dialogStage.initOwner(textUsuario.getScene().getWindow());
                System.out.println("7. Owner configurado");
            } else {
                System.out.println("7. Owner não disponível, continuando sem...");
            }

            System.out.println("8. Configurando Scene...");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Ajustar tamanho da janela
            dialogStage.setResizable(false);

            // Quando o diálogo for fechado, atualizar os dados
            dialogStage.setOnHidden(event -> {
                System.out.println("Diálogo fechado, recarregando dados...");
                carregarDados();
            });

            System.out.println("9. Mostrando diálogo...");
            dialogStage.showAndWait();
            System.out.println("10. Diálogo fechado");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao abrir diálogo de nova receita: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o diálogo de nova receita.\nErro: " + e.getMessage());
        }
    }

    private void abrirDialogoDespesa() {
        System.out.println("=== TENTANDO ABRIR DIALOGO DE NOVA DESPESA ===");
        try {
            System.out.println("1. Preparando FXMLLoader...");
            FXMLLoader loader = new FXMLLoader();

            // Tentar encontrar o arquivo FXML
            URL fxmlUrl = getClass().getResource("/view/pages/Dialogs/NovaDespesaDialog.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERRO: Arquivo FXML não encontrado: /view/pages/Dialogs/NovaDespesaDialog.fxml");

                // Tentar caminho alternativo
                fxmlUrl = getClass().getClassLoader().getResource("view/pages/Dialogs/NovaDespesaDialog.fxml");
                if (fxmlUrl == null) {
                    System.err.println("ERRO: Arquivo FXML também não encontrado no classloader");
                    mostrarAlerta("Erro", "Arquivo de diálogo não encontrado.\nCaminho: /view/pages/Dialogs/NovaDespesaDialog.fxml");
                    return;
                }
            }

            System.out.println("2. Arquivo FXML encontrado: " + fxmlUrl);
            loader.setLocation(fxmlUrl);

            System.out.println("3. Configurando Controller Factory...");
            if (springContext == null) {
                System.err.println("ERRO: Spring Context é nulo!");
                mostrarAlerta("Erro", "Spring Context não está disponível.");
                return;
            }

            loader.setControllerFactory(springContext::getBean);

            System.out.println("4. Carregando FXML...");
            Parent root;
            try {
                root = loader.load();
                System.out.println("5. FXML carregado com sucesso");
            } catch (Exception e) {
                System.err.println("ERRO ao carregar FXML: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Erro", "Falha ao carregar o arquivo de diálogo: " + e.getMessage());
                return;
            }

            System.out.println("6. Criando Stage...");
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nova Despesa");
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // Obter a janela atual de forma segura
            if (textUsuario != null && textUsuario.getScene() != null && textUsuario.getScene().getWindow() != null) {
                dialogStage.initOwner(textUsuario.getScene().getWindow());
                System.out.println("7. Owner configurado");
            } else {
                System.out.println("7. Owner não disponível, continuando sem...");
            }

            System.out.println("8. Configurando Scene...");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Ajustar tamanho da janela
            dialogStage.setResizable(false);

            // Quando o diálogo for fechado, atualizar os dados
            dialogStage.setOnHidden(event -> {
                System.out.println("Diálogo fechado, recarregando dados...");
                carregarDados();
            });

            System.out.println("9. Mostrando diálogo...");
            dialogStage.showAndWait();
            System.out.println("10. Diálogo fechado");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao abrir diálogo de nova despesa: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o diálogo de nova despesa.\nErro: " + e.getMessage());
        }
    }

    private void abrirDialogoNovoProcedimento() {
        System.out.println("=== ABRINDO DIALOGO DE NOVO PROCEDIMENTO ===");
        try {
            // Diálogo simples para cadastrar novo procedimento
            Dialog<Procedimento> dialog = new Dialog<>();
            dialog.setTitle("Novo Procedimento");
            dialog.setHeaderText("Cadastrar Novo Procedimento");

            // Configurar botões
            ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

            // Criar campos do formulário
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField nomeField = new TextField();
            nomeField.setPromptText("Nome do procedimento");
            TextField valorField = new TextField();
            valorField.setPromptText("Valor (ex: 100,00)");
            TextField tempoField = new TextField();
            tempoField.setPromptText("Tempo previsto (HH:MM)");
            TextField corField = new TextField();
            corField.setPromptText("Cor em HEX (ex: #09c6d9)");

            grid.add(new Label("Nome:"), 0, 0);
            grid.add(nomeField, 1, 0);
            grid.add(new Label("Valor:"), 0, 1);
            grid.add(valorField, 1, 1);
            grid.add(new Label("Tempo:"), 0, 2);
            grid.add(tempoField, 1, 2);
            grid.add(new Label("Cor (HEX):"), 0, 3);
            grid.add(corField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Converter resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == salvarButtonType) {
                    try {
                        // Validar campos
                        if (nomeField.getText().isEmpty() || valorField.getText().isEmpty()) {
                            mostrarAlerta("Erro", "Nome e valor são obrigatórios.");
                            return null;
                        }

                        // Criar novo procedimento
                        Procedimento novoProcedimento = Procedimento.builder()
                                .nome(nomeField.getText())
                                .valor(new BigDecimal(valorField.getText().replace(",", ".")))
                                .tempo_previsto(java.sql.Time.valueOf(tempoField.getText() + ":00"))
                                .corHex(corField.getText().isEmpty() ? "#09c6d9" : corField.getText())
                                .build();

                        System.out.println("Novo procedimento criado: " + novoProcedimento.getNome());
                        return novoProcedimento;
                    } catch (Exception e) {
                        System.err.println("Erro ao criar procedimento: " + e.getMessage());
                        mostrarAlerta("Erro", "Dados inválidos: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<Procedimento> result = dialog.showAndWait();

            result.ifPresent(procedimento -> {
                try {
                    System.out.println("Salvando novo procedimento no banco...");
                    procedimentoService.salvarProcedimento(procedimento);
                    mostrarAlerta("Sucesso", "Procedimento cadastrado com sucesso!");
                    carregarProcedimentosDoBanco();
                    carregarValoresProcedimentos();
                    System.out.println("Procedimento salvo com sucesso");
                } catch (Exception e) {
                    System.err.println("ERRO ao salvar procedimento: " + e.getMessage());
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao salvar procedimento: " + e.getMessage());
                }
            });

            System.out.println("Diálogo de novo procedimento fechado");

        } catch (Exception e) {
            System.err.println("ERRO ao abrir diálogo de novo procedimento: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o diálogo de novo procedimento: " + e.getMessage());
        }
    }
}