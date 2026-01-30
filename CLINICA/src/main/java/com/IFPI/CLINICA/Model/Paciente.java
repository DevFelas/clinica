package com.IFPI.CLINICA.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "tbPaciente",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "cpf")
        }
)
public class Paciente {

    public Paciente( String nome, String cpf, LocalDate dataNascimento, String contato, String rua, String bairro, String cidade, String numero) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.contato = contato;
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.numero = numero;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    @JsonProperty("dataNascimento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dataNascimento;

    private String contato;
    private String rua;
    private String bairro;
    private String cidade;
    private String numero;

}
