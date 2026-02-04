package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface de persistência para a entidade Procedimento.
 * Esta interface estende JpaRepository para herdar o conjunto completo de
 * operações de manipulação de dados. Como os requisitos atuais
 * utilizam apenas as buscas padrão por identificador primário e listagem
 * geral, não há necessidade de definir métodos de consulta customizados.
 */
public interface ProcedimentoRepository extends JpaRepository<Procedimento, Integer>{ }
