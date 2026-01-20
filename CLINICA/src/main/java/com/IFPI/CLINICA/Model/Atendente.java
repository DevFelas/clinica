package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbAtendente")
public class Atendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

}
