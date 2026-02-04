package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Repository.ProcedimentoRepository;
import com.IFPI.CLINICA.Repository.TransacaoFinanceiraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FinanceiroService {

    @Autowired
    private TransacaoFinanceiraRepository repository;

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    public List<TransacaoFinanceira> listarTransacoes(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            return repository.findByDataBetween(inicio, fim);
        }
        return repository.findAll();
    }

    public BigDecimal calcularTotalEntradas(LocalDate inicio, LocalDate fim) {
        List<TransacaoFinanceira> transacoes = listarTransacoes(inicio, fim);
        return transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.ENTRADA)
                .filter(t -> t.getStatus() != StatusTransacao.CANCELADO) // Ignora canceladas
                .map(TransacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalSaidas(LocalDate inicio, LocalDate fim) {
        List<TransacaoFinanceira> transacoes = listarTransacoes(inicio, fim);
        return transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.SAIDA)
                .filter(t -> t.getStatus() != StatusTransacao.CANCELADO) // Ignora canceladas
                .map(TransacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularLucro(LocalDate inicio, LocalDate fim) {
        return calcularTotalEntradas(inicio, fim).subtract(calcularTotalSaidas(inicio, fim));
    }

    public TransacaoFinanceira criarTransacao(TransacaoFinanceira transacao) {
        try {
            System.out.println("Salvando transação: " + transacao.getDescricao() +
                    " | Valor: " + transacao.getValor() +
                    " | Tipo: " + transacao.getTipo());
            return repository.save(transacao);
        } catch (Exception e) {
            System.err.println("Erro ao salvar transação: " + e.getMessage());
            throw e;
        }
    }

    public TransacaoFinanceira atualizarStatus(Integer id, StatusTransacao status) {
        try {
            TransacaoFinanceira transacao = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transação não encontrada com ID: " + id));

            System.out.println("Atualizando status da transação ID " + id +
                    " de " + transacao.getStatus() + " para " + status);

            transacao.setStatus(status);
            TransacaoFinanceira atualizada = repository.save(transacao);

            System.out.println("Status atualizado com sucesso!");
            return atualizada;

        } catch (Exception e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
            throw e;
        }
    }

    public void excluirTransacao(Integer id) {
        repository.deleteById(id);
    }

    // Método adicional para obter transação por ID
    public TransacaoFinanceira buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com ID: " + id));
    }

    public Procedimento atualizarProcedimentoPorId(Integer id, Procedimento dados) {
        Procedimento p = procedimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado: " + id));

        p.setNome(dados.getNome());
        p.setValor(dados.getValor());
        p.setTempo_previsto(dados.getTempo_previsto());
        p.setCorHex(dados.getCorHex());

        return procedimentoRepository.save(p);
    }

    public List<Procedimento> listarTodos() {
        return procedimentoRepository.findAll();
    }

}