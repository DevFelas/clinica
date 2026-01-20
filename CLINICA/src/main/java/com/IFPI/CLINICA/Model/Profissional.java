package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbProfissional")
public class Profissional   {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProfissional;

    @Column(nullable = false)
    private Integer cro;

    @Column(nullable = false, length = 100)
    private String nome;

}
