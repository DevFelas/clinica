package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa os operadores do sistema clínico.
 * Esta classe armazena as credenciais de autenticação e o nível de autorização (Perfil)
 * de cada colaborador, servindo como base para a camada de segurança e controle
 * de acesso à aplicação.
 */
@Entity
@Table(name = "tbUsuarios")
public class Usuario {

    /**
     * Identificador único do usuário, gerado automaticamente pela estratégia de identidade do banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Identificador de acesso (nome de usuário) utilizado no processo de autenticação.
     */
    @Getter
    @Setter
    private String login;

    /**
     * Chave de segurança para validação da identidade do usuário.
     * Note que o acesso de leitura (Getter) é omitido para aumentar a segurança do atributo.
     */
    @Setter
    private String senha;

    /**
     * Define o nível de privilégios do usuário no sistema (ex: ADMIN, RECEPCIONISTA).
     * Armazenado como String no banco de dados para garantir a legibilidade dos registros.
     */
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Perfil perfil;
}