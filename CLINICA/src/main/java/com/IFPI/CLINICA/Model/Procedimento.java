package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;
import lombok .*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbProcedimento")
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private Time tempo_previsto;

    @OneToMany(mappedBy = "procedimento")
    private List<Agendamento> agendamentos;
}
