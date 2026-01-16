package com.IFPI.CLINICA.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tbPaciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaciente;

    @Column(nullable = false)
    private Integer cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    private LocalDate dataNascimento;

    private String contato;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<EnderecoPaciente> enderecos;

}
