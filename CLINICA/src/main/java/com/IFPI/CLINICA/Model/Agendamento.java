package com.IFPI.CLINICA.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
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

//    @ManyToOne
//    @JoinColumn(name = "Profissional", nullable = false)
//    private Profissional profissional;
//
//    @ManyToOne
//    @JoinColumn(name = "Atendente", nullable = false)
//    private Atendente atendente;



    @ManyToOne
    @JoinColumn(name = "Paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "Procedimento", nullable = false)
    private Procedimento procedimento;

    //GETTERS E SETTERS
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Procedimento getProcedimento() { return procedimento; }
    public void setProcedimento(Procedimento procedimento) { this.procedimento = procedimento; }

    public StatusAgendamento getStatus() {
        return status;
    }
    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

}
