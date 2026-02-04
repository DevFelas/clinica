package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Util.Navigator;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Service.FinanceiroService;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller responsável pela gestão financeira da clínica.
 * Esta classe atua como ponte entre a interface (FXML), os serviços de negócio (Spring)
 * e o banco de dados para gerenciar receitas, despesas e preços de procedimentos.
 */
@Component
public class FinanceiroController extends SuperController implements Initializable {

    @Autowired private Navigator navigator;
    @Autowired private FinanceiroService financeiroService;
    @Autowired private ProcedimentoService procedimentoService;
    @Autowired private ApplicationContext springContext;

    // Campos do resumo financeiro
    @FXML private TextField totalReceitasField;
    @FXML private TextField lucroField;
    @FXML private TextField totalDespesasField;

    // Campos dos procedimentos
    @FXML private VBox procedimentosBox;
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
    @FXML private Label textUsuario;
    @FXML private Button btnFinanceiro;

    private Procedimento procedimentoSelecionado = null;

    private ObservableList<TransacaoFinanceira> transacoes = FXCollections.observableArrayList();
    private NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Mapa para cache local de procedimentos, facilitando a atualização de preços
     * e o mapeamento entre o User Interface e os objetos do modelo.
     */
    private Map<String, Procedimento> procedimentosMap = new HashMap<>();

    /**
     * Método de inicialização acionado automaticamente pelo JavaFX.
     * Realiza a configuração de segurança, definições de data padrão,
     * inicialização de listeners e carga inicial de dados.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            aplicarPermissoesUI(btnFinanceiro, textUsuario);

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
            carregarProcedimentos();

            System.out.println("=== FINANCEIRO CONTROLLER INICIALIZADO COM SUCESSO ===");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Inicialização", "Não foi possível inicializar a tela financeira: " + e.getMessage());
        }
    }

    /**
     * Carrega a lista de procedimentos e renderiza cada item na {@code procedimentosBox}.
     *
     * <p>Primeiro remove todos os nós existentes no container, em seguida busca os
     * procedimentos no {@code procedimentoService} e cria uma linha (HBox) para cada
     * procedimento, adicionando-a ao container.</p>
     */
    private void carregarProcedimentos() {
        procedimentosBox.getChildren().clear();

        List<Procedimento> lista = procedimentoService.listarProcedimentos();

        for (Procedimento p : lista) {
            HBox linha = criarLinhaProcedimento(p);
            procedimentosBox.getChildren().add(linha);
        }
    }

    /**
     * Cria a representação visual (linha) de um procedimento para exibição na tela.
     *
     * <p>A linha contém:
     * <ul>
     *   <li>um {@link javafx.scene.control.Label} com o nome do procedimento</li>
     *   <li>um {@link javafx.scene.layout.Region} para "empurrar" o preço para a direita</li>
     *   <li>um {@link javafx.scene.control.TextField} não editável com o valor formatado</li>
     * </ul>
     * </p>
     *
     * <p>Também adiciona um handler de clique para permitir selecionar o procedimento,
     * chamando {@link #selecionarProcedimento(HBox, Procedimento)}.</p>
     *
     * @param p procedimento que será representado na interface
     * @return {@link javafx.scene.layout.HBox} configurado e pronto para ser adicionado ao layout
     */
    private HBox criarLinhaProcedimento(Procedimento p) {
        Label nome = new Label(p.getNome());

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        TextField preco = new TextField(formatarDinheiro(p.getValor()));
        preco.setEditable(false);
        preco.setFocusTraversable(false);
        preco.setFocusTraversable(false);

        HBox linha = new HBox(8, nome, espaco, preco);
        linha.setStyle("-fx-padding: 6; -fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6;");

        // ✅ torna clicável e seleciona
        linha.setOnMouseClicked(e -> selecionarProcedimento(linha, p));

        return linha;
    }

    /**
     * Referência para a linha (HBox) atualmente selecionada na lista de procedimentos.
     *
     * <p>É usada para remover o destaque visual do item anterior quando um novo
     * procedimento for selecionado.</p>
     */
    private HBox linhaSelecionada = null;

    /**
     * Seleciona um procedimento na interface, aplicando destaque visual na linha clicada
     * e removendo o destaque do item selecionado anteriormente (se houver).
     *
     * <p>Além do destaque, também atualiza a variável {@code procedimentoSelecionado}
     * para refletir o procedimento escolhido pelo usuário.</p>
     *
     * @param linha a {@link javafx.scene.layout.HBox} que representa o procedimento clicado
     * @param p o {@link Procedimento} associado à linha clicada
     */
    private void selecionarProcedimento(HBox linha, Procedimento p) {
        // tira destaque do anterior
        if (linhaSelecionada != null) {
            linhaSelecionada.setStyle("-fx-padding: 6; -fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6;");
        }

        // destaca o atual
        linha.setStyle("-fx-padding: 6; -fx-border-color: #2cb784; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");
        linhaSelecionada = linha;

        procedimentoSelecionado = p;
    }

    /**
     * Formata um valor numérico no padrão monetário brasileiro (R$), usando locale {@code pt-BR}.
     *
     * <p>Exemplos:
     * <ul>
     *   <li>{@code 10} → {@code "R$ 10,00"}</li>
     *   <li>{@code 199.9} → {@code "R$ 199,90"}</li>
     * </ul>
     * </p>
     *
     * @param valor valor numérico a ser formatado (ex.: {@link java.lang.Integer}, {@link java.lang.Double})
     * @return string no formato de moeda brasileira
     */
    private String formatarDinheiro(Number valor) {
        // se seu valor for BigDecimal, melhor ainda: troque Number por BigDecimal
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        return nf.format(valor);
    }

    /**
     * Configura a lógica de exibição e formatação da TableView financeira.
     * Este método define como os atributos do objeto TransacaoFinanceira são extraídos
     * e transformados em texto para o usuário, além de aplicar regras visuais de cores.
     */
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

    /**
     * Recupera as movimentações financeiras do banco de dados e atualiza os indicadores da interface.
     * Este método realiza o processamento de datas, consulta ao service e formatação visual
     * do lucro (positivo/negativo) e dos valores monetários.
     */
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

            // Forçar atualização da tabela
            tabelaFinanceiro.refresh();

            System.out.println("=== DADOS CARREGADOS COM SUCESSO ===");

        } catch (Exception e) {
            System.err.println("ERRO ao carregar dados financeiros: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível carregar os dados financeiros: " + e.getMessage());
        }
    }

    /**
     * Atualiza os componentes visuais de detalhes (Labels) com base na transação selecionada na tabela.
     * Este método implementa uma lógica de segurança para tratar campos nulos (N/A) e
     * altera a cor da Label de status para fornecer feedback visual imediato.
     *
     * @param transacao O objeto TransacaoFinanceira cujos dados serão exibidos.
     */
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

    /**
     * Reseta os campos de texto do painel de detalhes para o estado inicial.
     * Utilizado quando nenhuma transação está selecionada na tabela para evitar a exibição
     * de informações residuais de seleções anteriores.
     */
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
    public void irPara(ActionEvent event) {
        super.irPara(event);
    }

    @FXML
    public void sair(ActionEvent event) {
        super.sair(event);
    }

    // ==================== MÉTODOS DOS BOTÕES DIREITOS ====================
    /**
     * Aciona a abertura do diálogo para criação de uma nova entrada financeira (Receita).
     */
    @FXML private void onNovaReceita() {abrirDialogoReceita();}
    /**
     * Aciona a abertura do diálogo para criação de uma nova saída financeira (Despesa).
     */
    @FXML private void onNovaDespesa() {abrirDialogoDespesa();}
    /**
     * Aciona a abertura do diálogo para cadastro de um novo tipo de procedimento e seu valor.
     */
    @FXML private void onNovoProcedimento() {
        abrirDialogoNovoProcedimento();
    }

    /**
     * Gera e exibe um resumo estatístico das transações com base no período selecionado.
     * O método calcula somatórios e utiliza Java Streams para filtrar quantidades de transações
     * por status (Pagas, Pendentes, Canceladas) antes de exibir em um Alert informativo.
     */
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

    /**
     * Prepara os dados financeiros para exportação.
     * Atualmente, o método realiza uma simulação de exportação gerando uma representação
     * textual estruturada de todas as transações visíveis na tabela dentro do período
     * selecionado, exibindo-as em uma interface de visualização (TextArea) com suporte a rolagem.
     */
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

    public void onActionEditarProcedimento() {
        if (procedimentoSelecionado == null) {
            mostrarAlerta("Atenção", "Selecione um procedimento primeiro.");
            return;
        }
        abrirDialogoEditarProcedimento(procedimentoSelecionado);
    }

    private void abrirDialogoEditarProcedimento(Procedimento proc) {
        try {
            if (proc.getCorHex() == null || proc.getCorHex().trim().isEmpty()) {
                proc.setCorHex("#09c6d9");
            }

            Dialog<Procedimento> dialog = new Dialog<>();
            dialog.setTitle("Editar Procedimento");
            dialog.setHeaderText("Editar: " + proc.getNome());

            ButtonType salvarButtonType = new ButtonType("Salvar alterações", ButtonBar.ButtonData.OK_DONE);
            ButtonType removerButtonType = new ButtonType("Remover", ButtonBar.ButtonData.OTHER);

            dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, removerButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField nomeField = new TextField(proc.getNome());
            TextField valorField = new TextField(proc.getValor() != null ? proc.getValor().toString().replace(".", ",") : "");
            TextField tempoField = new TextField(
                    proc.getTempo_previsto() != null
                            ? proc.getTempo_previsto().toLocalTime().toString().substring(0, 5) // HH:MM
                            : ""
            );
            TextField corField = new TextField(proc.getCorHex() != null ? proc.getCorHex() : "#09c6d9");

            grid.add(new Label("Nome:"), 0, 0);
            grid.add(nomeField, 1, 0);
            grid.add(new Label("Valor:"), 0, 1);
            grid.add(valorField, 1, 1);
            grid.add(new Label("Tempo:"), 0, 2);
            grid.add(tempoField, 1, 2);
            grid.add(new Label("Cor (HEX):"), 0, 3);
            grid.add(corField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            Button btnRemover = (Button) dialog.getDialogPane().lookupButton(removerButtonType);
            btnRemover.setOnAction(evt -> {
                evt.consume();

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmar exclusão");
                confirm.setHeaderText("Excluir o procedimento \"" + proc.getNome() + "\"?");
                confirm.setContentText("Essa ação não pode ser desfeita.");

                Optional<ButtonType> resp = confirm.showAndWait();
                if (resp.isPresent() && resp.get() == ButtonType.OK) {
                    try {
                        procedimentoService.deletarProcedimentoPorId(proc.getId());

                        mostrarAlerta("Sucesso", "Procedimento removido com sucesso!");
                        dialog.close();

                        carregarProcedimentos();
                        procedimentoSelecionado = null;
                        linhaSelecionada = null;

                    } catch (Exception e) {
                        e.printStackTrace();
                        String msg = e.getMessage();
                        if (msg != null && msg.contains("Referential integrity constraint violation")) {
                            mostrarAlerta("Não é possível remover",
                                    "Esse procedimento já está vinculado a agendamentos.\n");
                        } else {
                            mostrarAlerta("Erro", "Erro ao remover procedimento: " + e.getMessage());
                        }
                    }
                }
            });

            dialog.setResultConverter(btn -> {
                if (btn == salvarButtonType) {
                    if (nomeField.getText().trim().isEmpty() || valorField.getText().trim().isEmpty()) {
                        mostrarAlerta("Erro", "Nome e valor são obrigatórios.");
                        return null;
                    }

                    try {
                        BigDecimal novoValor = new BigDecimal(
                                valorField.getText().trim().replace(".", "").replace(",", ".")
                        );

                        proc.setNome(nomeField.getText().trim());
                        proc.setValor(novoValor);

                        if (!tempoField.getText().trim().isEmpty()) {
                            proc.setTempo_previsto(java.sql.Time.valueOf(tempoField.getText().trim() + ":00"));
                        }

                        String cor = corField.getText();
                        cor = (cor == null) ? "" : cor.trim();
                        if (cor.isEmpty()) cor = "#09c6d9";
                        proc.setCorHex(cor);


                        return proc;
                    } catch (Exception e) {
                        mostrarAlerta("Erro", "Dados inválidos: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<Procedimento> result = dialog.showAndWait();

            result.ifPresent(editado -> {
                try {
                    procedimentoService.atualizarProcedimentoPorId(editado.getId(), editado);

                    mostrarAlerta("Sucesso", "Procedimento atualizado com sucesso!");
                    carregarProcedimentos();
                    procedimentoSelecionado = null;
                    linhaSelecionada = null;

                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao salvar alterações: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o diálogo: " + e.getMessage());
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

    /**
     * Abre um diálogo modal para cadastrar um novo {@link Procedimento} via interface JavaFX.
     *
     * <p>O diálogo exibe um formulário com campos de:
     * <ul>
     *   <li><b>Nome</b> (obrigatório)</li>
     *   <li><b>Valor</b> (obrigatório, aceitando vírgula ou ponto como separador decimal)</li>
     *   <li><b>Tempo previsto</b> (formato esperado: {@code HH:MM})</li>
     *   <li><b>Cor em HEX</b> (opcional; se vazio, aplica uma cor padrão)</li>
     * </ul>
     * </p>
     *
     * <p>Ao clicar em <b>Salvar</b>, os dados são validados e convertidos para um objeto
     * {@link Procedimento}. Se a conversão falhar (ex.: valor inválido, tempo no formato incorreto),
     * um alerta é exibido e o resultado do diálogo é {@code null}.</p>
     *
     * <p>Se o usuário confirmar e o objeto for criado com sucesso, o procedimento é persistido
     * chamando {@code procedimentoService.salvarProcedimento(procedimento)} e um alerta de sucesso
     * é exibido. Qualquer erro de persistência também é tratado com alerta.</p>
     *
     * <p><b>Observações importantes:</b>
     * <ul>
     *   <li>O campo de tempo usa {@link java.sql.Time#valueOf(String)} e exige o formato {@code HH:MM:SS};
     *       por isso o código concatena {@code ":00"} ao valor digitado.</li>
     *   <li>O valor monetário é convertido para {@link java.math.BigDecimal} após substituir vírgula por ponto.</li>
     *   <li>Se a cor HEX estiver vazia, utiliza {@code "#09c6d9"} como padrão.</li>
     * </ul>
     * </p>
     */
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