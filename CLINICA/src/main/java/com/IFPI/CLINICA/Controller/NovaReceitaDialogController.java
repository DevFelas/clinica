package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Service.clinicaTeste.FinanceiroService;
import com.IFPI.CLINICA.Service.ProcedimentoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class NovaReceitaDialogController implements Initializable {

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private ProcedimentoService procedimentoService;

    @FXML private DatePicker dataField;
    @FXML private TextField descricaoField;
    @FXML private TextField valorField;
    @FXML private ComboBox<CategoriaTransacao> categoriaCombo;
    @FXML private ComboBox<StatusTransacao> statusCombo;
    @FXML private ComboBox<String> tipoReceitaCombo;

    private final ObservableList<CategoriaTransacao> categorias =
            FXCollections.observableArrayList(CategoriaTransacao.values());

    private final ObservableList<StatusTransacao> statusList =
            FXCollections.observableArrayList(StatusTransacao.values());

    private ObservableList<String> tiposReceita = FXCollections.observableArrayList();
    private List<Procedimento> procedimentos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("=== NOVA RECEITA DIALOG INITIALIZE ===");

        // Configurar data atual
        dataField.setValue(LocalDate.now());

        // Carregar procedimentos do banco
        try {
            procedimentos = procedimentoService.listarProcedimentos();
            tiposReceita.clear();

            if (procedimentos != null && !procedimentos.isEmpty()) {
                for (Procedimento proc : procedimentos) {
                    tiposReceita.add(proc.getNome());
                    System.out.println("Procedimento carregado: " + proc.getNome() + " - R$ " + proc.getValor());
                }
            } else {
                System.out.println("Nenhum procedimento encontrado no banco");
            }

            tiposReceita.add("Outros");
            System.out.println("Total de procedimentos carregados: " + tiposReceita.size());

        } catch (Exception e) {
            System.err.println("Erro ao carregar procedimentos: " + e.getMessage());
            tiposReceita.add("Outros");
        }

        tipoReceitaCombo.setItems(tiposReceita);
        if (!tiposReceita.isEmpty()) {
            tipoReceitaCombo.setValue(tiposReceita.get(0));
        }

        categoriaCombo.setItems(categorias);
        categoriaCombo.setValue(CategoriaTransacao.CONSULTA);

        statusCombo.setItems(statusList);
        statusCombo.setValue(StatusTransacao.PAGO);

        configurarMascaraValor();
        configurarListenerTipoReceita();

        System.out.println("Diálogo de nova receita configurado com sucesso");
    }

    private void configurarListenerTipoReceita() {
        tipoReceitaCombo.setOnAction(e -> {
            String selecionado = tipoReceitaCombo.getValue();
            System.out.println("Procedimento selecionado: " + selecionado);

            if (selecionado != null && !"Outros".equals(selecionado)) {
                // Preencher descrição com o nome do procedimento
                descricaoField.setText(selecionado);

                // Buscar e preencher o valor do procedimento
                if (procedimentos != null) {
                    for (Procedimento proc : procedimentos) {
                        if (proc.getNome().equals(selecionado)) {
                            // Formatar valor para exibição (com vírgula)
                            String valorFormatado = String.format("%.2f", proc.getValor());
                            valorFormatado = valorFormatado.replace(".", ",");
                            valorField.setText(valorFormatado);

                            // Preencher categoria correspondente automaticamente
                            preencherCategoriaAutomaticamente(proc.getNome());
                            System.out.println("Valor preenchido: " + valorFormatado + " para " + proc.getNome());
                            break;
                        }
                    }
                }
            } else if ("Outros".equals(selecionado)) {
                // Limpar campos quando selecionar "Outros"
                descricaoField.clear();
                valorField.clear();
                categoriaCombo.setValue(CategoriaTransacao.OUTROS);
                System.out.println("Selecionado 'Outros', campos limpos");
            }
        });
    }

    private void preencherCategoriaAutomaticamente(String nomeProcedimento) {
        if (nomeProcedimento != null) {
            String nomeLower = nomeProcedimento.toLowerCase();
            System.out.println("Preenchendo categoria para: " + nomeLower);

            if (nomeLower.contains("consulta")) {
                categoriaCombo.setValue(CategoriaTransacao.CONSULTA);
            } else if (nomeLower.contains("limpeza")) {
                categoriaCombo.setValue(CategoriaTransacao.LIMPEZA);
            } else if (nomeLower.contains("exodontia")) {
                categoriaCombo.setValue(CategoriaTransacao.EXODONTIA);
            } else if (nomeLower.contains("prótese") || nomeLower.contains("protese")) {
                categoriaCombo.setValue(CategoriaTransacao.PROTESE);
            } else if (nomeLower.contains("implante")) {
                categoriaCombo.setValue(CategoriaTransacao.IMPLANTE);
            } else if (nomeLower.contains("manutenção") || nomeLower.contains("manutencao")) {
                categoriaCombo.setValue(CategoriaTransacao.MANUTENCAO);
            } else if (nomeLower.contains("montagem")) {
                categoriaCombo.setValue(CategoriaTransacao.MONTAGEM);
            } else if (nomeLower.contains("restauração") || nomeLower.contains("restauracao")) {
                categoriaCombo.setValue(CategoriaTransacao.RESTAURACAO);
            } else {
                categoriaCombo.setValue(CategoriaTransacao.OUTROS);
            }
            System.out.println("Categoria definida: " + categoriaCombo.getValue());
        }
    }

    private void configurarMascaraValor() {
        valorField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*([.,]\\d{0,2})?")) {
                valorField.setText(oldVal);
            }
        });
    }

    @FXML
    private void salvar() {
        System.out.println("=== TENTANDO SALVAR NOVA RECEITA ===");
        if (!validarFormulario()) {
            System.out.println("Validação do formulário falhou");
            return;
        }

        try {
            // Converter valor para BigDecimal
            String valorTexto = valorField.getText().replace(",", ".");
            if (valorTexto.isEmpty()) {
                valorTexto = "0.00";
            }
            BigDecimal valor = new BigDecimal(valorTexto);

            // Criar transação usando o builder
            TransacaoFinanceira transacao = TransacaoFinanceira.builder()
                    .data(dataField.getValue())
                    .dataCadastro(LocalDateTime.now())
                    .descricao(descricaoField.getText())
                    .categoria(categoriaCombo.getValue())
                    .tipo(TipoTransacao.ENTRADA)
                    .status(statusCombo.getValue())
                    .valor(valor)
                    .build();

            // Adicione um log para verificar
            System.out.println("Salvando receita:");
            System.out.println("  - Descrição: " + transacao.getDescricao());
            System.out.println("  - Valor: " + transacao.getValor());
            System.out.println("  - Tipo: " + transacao.getTipo());
            System.out.println("  - Categoria: " + transacao.getCategoria());
            System.out.println("  - Status: " + transacao.getStatus());
            System.out.println("  - Data: " + transacao.getData());

            TransacaoFinanceira saved = financeiroService.criarTransacao(transacao);
            System.out.println("Receita salva com ID: " + saved.getId());

            alertSucesso("Receita cadastrada com sucesso!");

            // Fechar a janela
            Stage stage = (Stage) dataField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("Erro ao salvar receita: " + e.getMessage());
            e.printStackTrace();
            alertErro("Erro ao salvar receita", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        System.out.println("Cancelando criação de receita");
        Stage stage = (Stage) dataField.getScene().getWindow();
        stage.close();
    }

    private boolean validarFormulario() {
        StringBuilder erros = new StringBuilder();

        if (dataField.getValue() == null) {
            erros.append("• Data é obrigatória\n");
            System.out.println("Data não preenchida");
        }

        if (descricaoField.getText() == null || descricaoField.getText().isBlank()) {
            erros.append("• Nome / Descrição é obrigatório\n");
            System.out.println("Descrição não preenchida");
        }

        if (valorField.getText() == null || valorField.getText().isBlank()) {
            erros.append("• Valor é obrigatório\n");
            System.out.println("Valor não preenchido");
        }

        if (categoriaCombo.getValue() == null) {
            erros.append("• Categoria é obrigatória\n");
            System.out.println("Categoria não selecionada");
        }

        if (statusCombo.getValue() == null) {
            erros.append("• Status é obrigatório\n");
            System.out.println("Status não selecionado");
        }

        // Validar valor numérico
        if (!valorField.getText().isBlank()) {
            try {
                String valorTexto = valorField.getText().replace(",", ".");
                new BigDecimal(valorTexto);
            } catch (NumberFormatException e) {
                erros.append("• Valor deve ser um número válido\n");
                System.out.println("Valor não é numérico: " + valorField.getText());
            }
        }

        if (!erros.isEmpty()) {
            alertAviso("Validação", erros.toString());
            return false;
        }

        System.out.println("Validação do formulário OK");
        return true;
    }

    private void alertSucesso(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertErro(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(titulo);
        a.showAndWait();
    }

    private void alertAviso(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.setTitle(titulo);
        a.showAndWait();
    }
}