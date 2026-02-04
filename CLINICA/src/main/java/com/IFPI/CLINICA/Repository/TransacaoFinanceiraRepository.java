package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.TransacaoFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface de acesso a dados para a entidade TransacaoFinanceira.
 * Além das operações padrão de persistência, esta interface implementa lógica
 * agregadora para suporte aos relatórios financeiros e balanço de caixa dentro
 * de períodos específicos.
 */
public interface TransacaoFinanceiraRepository extends JpaRepository<TransacaoFinanceira, Integer> {

    /**
     * Recupera todos os registros de transações ocorridos dentro de um intervalo de datas.
     * @param inicio Data inicial do período de busca.
     * @param fim Data final do período de busca.
     * @return Lista de transações financeiras cronologicamente filtradas.
     */
    List<TransacaoFinanceira> findByDataBetween(LocalDate inicio, LocalDate fim);

    /**
     * Calcula a soma total de valores registrados como créditos (ENTRADA) em um dado período.
     * Utiliza uma consulta personalizada em JPQL para realizar a agregação diretamente no banco de dados.
     * @param inicio Início do intervalo solicitado.
     * @param fim Término do intervalo solicitado.
     * @return O somatório total das entradas no formato BigDecimal.
     */
    @Query("SELECT SUM(t.valor) FROM TransacaoFinanceira t WHERE t.tipo = 'ENTRADA' AND t.data BETWEEN :inicio AND :fim")
    BigDecimal sumEntradasByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    /**
     * Calcula a soma total de valores registrados como débitos (SAIDA) em um dado período.
     * Permite a extração de métricas de despesas para análise de fluxo de caixa.
     * @param inicio Início do intervalo solicitado.
     * @param fim Término do intervalo solicitado.
     * @return O somatório total das saídas no formato BigDecimal.
     */
    @Query("SELECT SUM(t.valor) FROM TransacaoFinanceira t WHERE t.tipo = 'SAIDA' AND t.data BETWEEN :inicio AND :fim")
    BigDecimal sumSaidasByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}