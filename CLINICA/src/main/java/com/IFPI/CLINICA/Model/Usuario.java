package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tbUsuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Setter
    private String login;

    @Setter
    private String senha;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Perfil perfil;
}
