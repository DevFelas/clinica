package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbTransacaoFinanceira")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Data do serviço / agendamento */
    @Column(nullable = false)
    private LocalDate data;

    /** Data e hora em que a transação foi registrada no sistema */
    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "procedimento_id")
    private Procedimento procedimento;

    @Enumerated(EnumType.STRING)
    private CategoriaTransacao categoria;

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

}
