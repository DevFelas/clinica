package com.IFPI.CLINICA.Service.clinicaTeste;

import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Repository.TransacaoFinanceiraRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceiroService {

    @Autowired
    private TransacaoFinanceiraRepository repository;

    @PostConstruct
    public void init() {
        try {
            // Verificar se há dados no banco
            long count = repository.count();
            System.out.println("=== FINANCEIRO SERVICE INIT ===");
            System.out.println("Total de transações no banco: " + count);

            // Se não houver dados, criar alguns de teste
            if (count == 0) {
                System.out.println("Populando dados de teste...");

                // Receita de exemplo
                TransacaoFinanceira receita = TransacaoFinanceira.builder()
                        .data(LocalDate.now().minusDays(5))
                        .dataCadastro(LocalDateTime.now())
                        .descricao("Consulta com João Silva")
                        .tipo(TipoTransacao.ENTRADA)
                        .valor(new BigDecimal("150.00"))
                        .status(StatusTransacao.PAGO)
                        .categoria(CategoriaTransacao.CONSULTA)
                        .build();
                repository.save(receita);

                // Outra receita
                TransacaoFinanceira receita2 = TransacaoFinanceira.builder()
                        .data(LocalDate.now().minusDays(3))
                        .dataCadastro(LocalDateTime.now())
                        .descricao("Limpeza com Maria Santos")
                        .tipo(TipoTransacao.ENTRADA)
                        .valor(new BigDecimal("120.00"))
                        .status(StatusTransacao.PAGO)
                        .categoria(CategoriaTransacao.LIMPEZA)
                        .build();
                repository.save(receita2);

                // Despesa de exemplo
                TransacaoFinanceira despesa = TransacaoFinanceira.builder()
                        .data(LocalDate.now().minusDays(2))
                        .dataCadastro(LocalDateTime.now())
                        .descricao("Compra de materiais dentários")
                        .tipo(TipoTransacao.SAIDA)
                        .valor(new BigDecimal("89.50"))
                        .status(StatusTransacao.PENDENTE)
                        .categoria(CategoriaTransacao.INSUMOS)
                        .build();
                repository.save(despesa);

                // Outra despesa
                TransacaoFinanceira despesa2 = TransacaoFinanceira.builder()
                        .data(LocalDate.now().minusDays(1))
                        .dataCadastro(LocalDateTime.now())
                        .descricao("Pagamento de aluguel")
                        .tipo(TipoTransacao.SAIDA)
                        .valor(new BigDecimal("1200.00"))
                        .status(StatusTransacao.PAGO)
                        .categoria(CategoriaTransacao.ALUGUEL)
                        .build();
                repository.save(despesa2);

                System.out.println("Dados de teste populados com sucesso! Total: " + repository.count());
            }
        } catch (Exception e) {
            System.err.println("Erro ao popular dados de teste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<TransacaoFinanceira> listarTransacoes(LocalDate inicio, LocalDate fim) {
        try {
            System.out.println("=== FinanceiroService.listarTransacoes ===");
            System.out.println("Período: " + inicio + " até " + fim);

            List<TransacaoFinanceira> transacoes;
            if (inicio != null && fim != null) {
                transacoes = repository.findByDataBetween(inicio, fim);
            } else {
                transacoes = repository.findAll();
            }

            System.out.println("Transações encontradas: " + transacoes.size());
            for (TransacaoFinanceira t : transacoes) {
                System.out.println("  - ID: " + t.getId() +
                        " | Desc: " + t.getDescricao() +
                        " | Valor: " + t.getValor() +
                        " | Tipo: " + t.getTipo() +
                        " | Status: " + t.getStatus());
            }

            return transacoes;

        } catch (Exception e) {
            System.err.println("Erro em listarTransacoes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public BigDecimal calcularTotalEntradas(LocalDate inicio, LocalDate fim) {
        try {
            System.out.println("=== Calculando total de entradas ===");
            BigDecimal total;
            if (inicio != null && fim != null) {
                total = repository.sumEntradasByPeriodo(inicio, fim);
            } else {
                total = repository.findAll().stream()
                        .filter(t -> t.getTipo() == TipoTransacao.ENTRADA)
                        .map(TransacaoFinanceira::getValor)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            System.out.println("Total de entradas: " + (total != null ? total : BigDecimal.ZERO));
            return total != null ? total : BigDecimal.ZERO;

        } catch (Exception e) {
            System.err.println("Erro em calcularTotalEntradas: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calcularTotalSaidas(LocalDate inicio, LocalDate fim) {
        try {
            System.out.println("=== Calculando total de saídas ===");
            BigDecimal total;
            if (inicio != null && fim != null) {
                total = repository.sumSaidasByPeriodo(inicio, fim);
            } else {
                total = repository.findAll().stream()
                        .filter(t -> t.getTipo() == TipoTransacao.SAIDA)
                        .map(TransacaoFinanceira::getValor)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            System.out.println("Total de saídas: " + (total != null ? total : BigDecimal.ZERO));
            return total != null ? total : BigDecimal.ZERO;

        } catch (Exception e) {
            System.err.println("Erro em calcularTotalSaidas: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calcularLucro(LocalDate inicio, LocalDate fim) {
        BigDecimal entradas = calcularTotalEntradas(inicio, fim);
        BigDecimal saidas = calcularTotalSaidas(inicio, fim);
        BigDecimal lucro = entradas.subtract(saidas);
        System.out.println("Lucro calculado: " + entradas + " - " + saidas + " = " + lucro);
        return lucro;
    }

    public TransacaoFinanceira criarTransacao(TransacaoFinanceira transacao) {
        try {
            System.out.println("=== FinanceiroService.criarTransacao ===");
            System.out.println("Descrição: " + transacao.getDescricao());
            System.out.println("Tipo: " + transacao.getTipo());
            System.out.println("Valor: " + transacao.getValor());
            System.out.println("Categoria: " + transacao.getCategoria());
            System.out.println("Status: " + transacao.getStatus());
            System.out.println("Data: " + transacao.getData());

            TransacaoFinanceira saved = repository.save(transacao);
            System.out.println("Transação salva com ID: " + saved.getId());
            return saved;
        } catch (Exception e) {
            System.err.println("Erro em criarTransacao: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public TransacaoFinanceira atualizarStatus(Integer id, StatusTransacao status) {
        try {
            System.out.println("=== Atualizando status da transação ID: " + id + " para: " + status);
            TransacaoFinanceira transacao = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
            transacao.setStatus(status);
            TransacaoFinanceira atualizada = repository.save(transacao);
            System.out.println("Status atualizado com sucesso");
            return atualizada;
        } catch (Exception e) {
            System.err.println("Erro em atualizarStatus: " + e.getMessage());
            throw e;
        }
    }

    public void excluirTransacao(Integer id) {
        try {
            System.out.println("Excluindo transação ID: " + id);
            repository.deleteById(id);
            System.out.println("Transação excluída com sucesso");
        } catch (Exception e) {
            System.err.println("Erro em excluirTransacao: " + e.getMessage());
            throw e;
        }
    }
}