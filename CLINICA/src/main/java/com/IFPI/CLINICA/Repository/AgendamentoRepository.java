package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Agendamento;
import com.IFPI.CLINICA.Model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface de acesso a dados para a entidade Agendamento.
 * Estende JpaRepository para herdar operações fundamentais de persistência do CRUD
 * e utiliza o mecanismo de consultas derivadas (Query Methods) para buscas específicas
 * no banco de dados.
 */
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer>{

    /**
     * Recupera uma lista de agendamentos baseada em uma data específica.
     * @param data Objeto LocalDate representando o dia da consulta.
     * @return Coleção de agendamentos encontrados para a data informada.
     */
    List<Agendamento> findByData(LocalDate data);

    /**
     * Realiza a busca de agendamentos dentro de um intervalo cronológico.
     * @param inicio Data que delimita o começo do período.
     * @param fim Data que delimita o término do período.
     * @return Lista de registros compreendidos entre as duas datas.
     */
    List<Agendamento> findByDataBetween(
            LocalDate inicio,
            LocalDate fim
    );

    /**
     * Filtra agendamentos por data e por uma coleção de estados permitidos.
     * Este método permite buscar, todos os registros de hoje que
     * estejam simultaneamente nos estados "PENDENTE" ou "CONFIRMADO".
     * @param data Data do agendamento.
     * @param status Lista contendo os diferentes status para filtragem.
     * @return Lista de agendamentos que atendem a ambos os critérios.
     */
    List<Agendamento> findByDataAndStatusIn(
            LocalDate data,
            List<StatusAgendamento> status
    );
}
