package com.IFPI.CLINICA.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tbPaciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaciente;

    @Setter
    @Column(nullable = false)
    private Integer cpf;

    @Getter
    @Setter
    @Column(nullable = false, length = 100)
    private String nome;

    private LocalDate dataNascimento;

    @Getter
    @Setter
    private String contato;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<EnderecoPaciente> enderecos;

}
