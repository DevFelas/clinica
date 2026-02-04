package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface de acesso a dados para a entidade Paciente.
 * Ao estender JpaRepository, a interface herda funcionalidades para o gerenciamento
 * de persistência e busca, permitindo a interação com o banco de dados sem a
 * necessidade de escrita manual de consultas SQL complexas.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Integer>{

    /**
     * Define uma consulta personalizada para localizar um paciente através do seu CPF.
     * O uso do container Optional indica que o resultado pode ser nulo caso o
     * registro não exista, permitindo um tratamento de dados mais seguro por
     * parte das camadas superiores.
     * * @param cpf O número de registro único do paciente.
     * @return Um Optional contendo o paciente localizado ou vazio caso inexistente.
     */
    Optional<Paciente> findByCpf(String cpf);
}
