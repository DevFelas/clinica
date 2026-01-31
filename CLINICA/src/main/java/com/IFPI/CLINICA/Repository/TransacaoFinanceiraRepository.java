package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.TransacaoFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransacaoFinanceiraRepository extends JpaRepository<TransacaoFinanceira, Integer> {

    List<TransacaoFinanceira> findByDataBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(t.valor) FROM TransacaoFinanceira t WHERE t.tipo = 'ENTRADA' AND t.data BETWEEN :inicio AND :fim")
    BigDecimal sumEntradasByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT SUM(t.valor) FROM TransacaoFinanceira t WHERE t.tipo = 'SAIDA' AND t.data BETWEEN :inicio AND :fim")
    BigDecimal sumSaidasByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}