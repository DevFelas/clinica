package com.IFPI.CLINICA.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidade que representa os dados cadastrais de um Paciente.
 * Esta classe é mapeada para a tabela "tbPaciente" e inclui restrições de
 * integridade, como a unicidade do CPF, garantindo que não haja duplicidade
 * de registros no banco de dados.
 */
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
        })
public class Paciente {

    /**
     * Construtor customizado para inicialização dos atributos essenciais e de localização.
     */
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

    /**
     * Identificador primário gerado automaticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Documento de identificação único do paciente.
     */
    @Column(nullable = false)
    private String cpf;

    /**
     * Nome completo do paciente com limitação de caracteres.
     */
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Representa a data de nascimento, formatada para entrada e saída de dados via JSON.
     */
    @JsonProperty("dataNascimento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dataNascimento;

    private String contato;
    private String rua;
    private String bairro;
    private String cidade;
    private String numero;

    /**
     * Método auxiliar para apresentação visual do CPF com máscara (000.000.000-00).
     * @return String formatada ou o valor original caso não possua o tamanho padrão.
     */
    public String getCpfFormatado() {
        if (this.cpf == null || this.cpf.length() != 11) return this.cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    /**
     * Método auxiliar para apresentação visual do contato telefônico com máscara.
     * @return String formatada no padrão (86) 9 0000-0000.
     */
    public String getContatoFormatado() {
        if (this.contato == null || this.contato.length() != 11) return this.contato;
        return "(" + contato.substring(0, 2) + ") " + contato.substring(2, 3) +
                " " + contato.substring(3, 7) + "-" + contato.substring(7);
    }
}