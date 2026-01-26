package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer>{ }
