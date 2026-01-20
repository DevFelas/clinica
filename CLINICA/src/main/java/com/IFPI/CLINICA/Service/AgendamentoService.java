package com.IFPI.CLINICA.Service;


import com.IFPI.CLINICA.Model.StatusAgendamento;
import com.IFPI.CLINICA.Repository.AgendamentoRepository;
import org.springframework.stereotype.Service;
import com.IFPI.CLINICA.Model.Agendamento;

import java.util.List;


@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;

    public AgendamentoService(AgendamentoRepository repository){
        this.repository = repository;
    }

    //MARCAR AGENDAMENTO
    public Agendamento marcarAgendamento(Agendamento agendamento) {
        try {
            return repository.saveAndFlush(agendamento);
        } catch (Exception e) {
            System.out.println("Erro ao marcar agendamento: " + e.getMessage());
            return null;
        }
    }

    //LISTAR AGENDAMENTO
    public List<Agendamento> listarAgendamentos(){
        return repository.findAll();
    }

    //BUSCAR POR ID
    public Agendamento buscarPorId(Integer id){
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        } catch (RuntimeException e) {
            System.out.println("Erro ao buscar agendamento: " + e.getMessage());
            return null;
        }
    }

    //CANCELAR AGENDAMENTO
    public Agendamento cancelarAgendamento(Integer id){
        try {
            Agendamento agendamento = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

            agendamento.setStatus(StatusAgendamento.CANCELADA);

            return repository.saveAndFlush(agendamento);

        } catch (Exception e){
            System.out.println("Erro ao cancelar agendamento: " + e.getMessage());
            return null;
        }
    }

    //ALTERAR AGENDAMENTO
    public Agendamento alterarAgendamento(Integer id, Agendamento novoAgendamento) {
        try {
            Agendamento agendamentoExistente = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

            agendamentoExistente.setData(novoAgendamento.getData());
            agendamentoExistente.setHora(novoAgendamento.getHora());
            agendamentoExistente.setStatus(novoAgendamento.getStatus());

            return repository.saveAndFlush(agendamentoExistente);

        } catch (Exception e) {
            System.out.println("Erro ao alterar agendamento: " + e.getMessage());
            return null;
        }
    }

}





