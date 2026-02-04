package com.IFPI.CLINICA.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidade que representa o registro de agendamentos no sistema.
 * Esta classe define a estrutura da tabela "tbAgendamento" no banco de dados,
 * estabelecendo os relacionamentos entre pacientes, procedimentos e seus
 * respectivos estados e horários.
 */
@Entity
@Table(name = "tbAgendamento")
public class Agendamento {

    /**
     * Identificador único do agendamento, gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Estado atual do agendamento (ex: Confirmado, Cancelado), armazenado como String.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    /**
     * Data agendada para a realização do procedimento.
     */
    @Column(nullable = false)
    private LocalDate data;

    /**
     * Horário específico definido para o atendimento.
     */
    @Column(nullable = false)
    private LocalTime hora;

    /**
     * Associação com a entidade Paciente.
     * Indica que vários agendamentos podem pertencer a um único paciente.
     */
    @ManyToOne
    @JoinColumn(name = "Paciente", nullable = false)
    private Paciente paciente;

    /**
     * Associação com a entidade Procedimento.
     * Define qual serviço clínico será executado neste horário.
     */
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