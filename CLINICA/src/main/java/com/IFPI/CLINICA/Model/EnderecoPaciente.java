package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbEndereco_paciente")
public class EnderecoPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idenderecoPaciente;

    private String rua;
    private String bairro;
    private Integer numero;

    @ManyToOne
    @JoinColumn(name = "Paciente", nullable = false)
    private Paciente paciente;

}
