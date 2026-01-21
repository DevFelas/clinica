package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;


@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String login;
    private String senha;

    @Enumerated(EnumType.STRING)
    private Perfil perfil;
}
