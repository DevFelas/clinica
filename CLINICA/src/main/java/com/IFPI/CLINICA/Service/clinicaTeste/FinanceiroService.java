package com.IFPI.CLINICA.Service.clinicaTeste;

import com.IFPI.CLINICA.Model.*;
import com.IFPI.CLINICA.Repository.TransacaoFinanceiraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceiroService {

    @Autowired
    private TransacaoFinanceiraRepository repository;

    public List<TransacaoFinanceira> listarTransacoes(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            return repository.findByDataBetween(inicio, fim);
        }
        return repository.findAll();
    }

    public BigDecimal calcularTotalEntradas(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            BigDecimal total = repository.sumEntradasByPeriodo(inicio, fim);
            return total != null ? total : BigDecimal.ZERO;
        }
        return repository.findAll().stream()
                .filter(t -> t.getTipo() == TipoTransacao.ENTRADA)
                .map(TransacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalSaidas(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            BigDecimal total = repository.sumSaidasByPeriodo(inicio, fim);
            return total != null ? total : BigDecimal.ZERO;
        }
        return repository.findAll().stream()
                .filter(t -> t.getTipo() == TipoTransacao.SAIDA)
                .map(TransacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularLucro(LocalDate inicio, LocalDate fim) {
        return calcularTotalEntradas(inicio, fim).subtract(calcularTotalSaidas(inicio, fim));
    }

    public TransacaoFinanceira criarTransacao(TransacaoFinanceira transacao) {
        return repository.save(transacao);
    }

    public TransacaoFinanceira atualizarStatus(Integer id, StatusTransacao status) {
        TransacaoFinanceira transacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        transacao.setStatus(status);
        return repository.save(transacao);
    }

    public void excluirTransacao(Integer id) {
        repository.deleteById(id);
    }
}