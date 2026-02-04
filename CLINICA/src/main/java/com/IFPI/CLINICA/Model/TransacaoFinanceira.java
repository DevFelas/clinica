package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa o registro detalhado de movimentações financeiras.
 * Esta classe centraliza as informações de fluxo de caixa, permitindo a
 * rastreabilidade de receitas e despesas associadas a pacientes e procedimentos,
 * além de gerenciar metadados de auditoria como a data de cadastro automática.
 */
@Entity
@Table(name = "tbTransacaoFinanceira")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoFinanceira {

    /**
     * Identificador primário da transação, gerado automaticamente pela estratégia de identidade.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * * Data de competência do serviço ou agendamento que originou a transação.
     */
    @Column(nullable = false)
    private LocalDate data;

    /** * Carimbo de data e hora que registra o momento exato da inserção do registro no sistema.
     */
    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    /**
     * Descrição textual para identificação clara da natureza da transação.
     */
    @Column(nullable = false, length = 200)
    private String descricao;

    /**
     * Define a natureza contábil da movimentação (ENTRADA ou SAIDA).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    /**
     * Valor monetário da operação, utilizando BigDecimal para garantir a precisão decimal.
     */
    @Column(nullable = false)
    private BigDecimal valor;

    /**
     * Estado atual da transação (PAGO, PENDENTE ou CANCELADO).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    /**
     * Associação opcional com um Paciente, utilizada para identificar a origem de receitas.
     */
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    /**
     * Associação opcional com um Procedimento, permitindo a análise de rentabilidade por serviço.
     */
    @ManyToOne
    @JoinColumn(name = "procedimento_id")
    private Procedimento procedimento;

    /**
     * Classificação categórica da transação para fins de organização e relatórios.
     */
    @Enumerated(EnumType.STRING)
    private CategoriaTransacao categoria;

    /**
     * Método de ciclo de vida do JPA que assegura o preenchimento automático
     * do campo dataCadastro antes da persistência no banco de dados.
     */
    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }
}