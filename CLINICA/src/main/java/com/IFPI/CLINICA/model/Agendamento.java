package com.IFPI.CLINICA.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tbAgendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime hora;

    @ManyToOne
    @JoinColumn(name = "Profissional", nullable = false)
    private Profissional profissional;

    @ManyToOne
    @JoinColumn(name = "Atendente", nullable = false)
    private Atendente atendente;

    @ManyToOne
    @JoinColumn(name = "Paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "Procedimento", nullable = false)
    private Procedimento procedimento;
}
