package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbEndereco_paciente")
public class EnderecoPaciente {

    public EnderecoPaciente(String rua, String bairro, String cidade, String numero) {
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.numero = numero;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idenderecoPaciente;

    private String rua;
    private String bairro;
    private String cidade;
    private String numero;

    @ManyToOne
    @JoinColumn(name = "Paciente", nullable = false)
    private Paciente paciente;

}
