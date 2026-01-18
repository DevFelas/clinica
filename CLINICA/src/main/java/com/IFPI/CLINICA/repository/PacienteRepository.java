package com.IFPI.CLINICA.repository;

import com.IFPI.CLINICA.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer>{ }
