package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer>{

    List<Agendamento> findByData(LocalDate data);

    List<Agendamento> findByDataBetween(
            LocalDate inicio,
            LocalDate fim
    );

}
