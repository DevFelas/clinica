package com.IFPI.CLINICA.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tbProcedimento")
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProcedimento;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private BigDecimal valorPadrao;

}
