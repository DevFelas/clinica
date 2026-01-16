package com.IFPI.CLINICA.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbAtendente")
public class Atendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAtendente;

    @Column(nullable = false, length = 100)
    private String nome;

}
