package com.IFPI.CLINICA.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tbPaciente")
public class Paciente {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false)
    private Integer cpf;

    @Getter
    @Setter
    @Column(nullable = false, length = 100)
    private String nome;

    @Getter
    @Setter
    @JsonProperty("dataNascimento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @Getter
    @Setter
    private String contato;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<EnderecoPaciente> enderecos;

}
