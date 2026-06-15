package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Turmas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TurmasRepository extends JpaRepository<Turmas, Integer> {
}
