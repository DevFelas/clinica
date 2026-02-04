package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;
import lombok .*;

/**
 * Entidade que representa os serviços ou tratamentos oferecidos pela clínica.
 * Esta classe armazena informações críticas como precificação, tempo de execução
 * e identidade visual (cor) para exibição em interfaces de calendário.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbProcedimento")
public class Procedimento {

    /**
     * Identificador primário da entidade, gerenciado de forma incremental pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nomenclatura descritiva do procedimento clínico.
     */
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Valor monetário do serviço, utilizando BigDecimal para assegurar a precisão financeira.
     */
    @Column(nullable = false)
    private BigDecimal valor;

    /**
     * Estimativa de duração do procedimento para fins de organização da agenda.
     */
    @Column(nullable = false)
    private Time tempo_previsto;

    /**
     * Código hexadecimal que define a cor de representação do procedimento na interface gráfica.
     */
    @Column(nullable = false)
    private String corHex; // ex: #09c6d9

    /**
     * Relação bidirecional que lista todos os agendamentos associados a este procedimento específico.
     */
    @OneToMany(mappedBy = "procedimento")
    private List<Agendamento> agendamentos;

    /**
     * Sobrescrita do método toString para retornar o nome do procedimento,
     * facilitando a exibição em componentes de seleção (ComboBox) da interface.
     */
    @Override
    public String toString() {
        return nome;
    }
}