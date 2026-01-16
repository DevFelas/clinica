package com.IFPI.CLINICA.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Consulta_has_procedimento")

public class ConsultaProcedimento {

    @EmbeddedId
    private ConsultaProcedimentoId id;

    @ManyToOne
    @MapsId("consulta")
    @JoinColumn(name = "consulta")
    private Consulta consulta;

    @ManyToOne
    @MapsId("procedimento")
    @JoinColumn(name = "procedimento")
    private Procedimento procedimento;

    private BigDecimal valorCobrado;

}
