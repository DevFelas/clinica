package com.IFPI.CLINICA.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbPaciente")
public class Paciente {

    @Id
    @Column(nullable = false)
    private Integer cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    @JsonProperty("dataNascimento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dataNascimento;

    private String contato;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<EnderecoPaciente> enderecos;

}
