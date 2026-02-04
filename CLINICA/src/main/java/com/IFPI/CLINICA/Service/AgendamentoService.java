package com.IFPI.CLINICA.Service;


import com.IFPI.CLINICA.Model.StatusAgendamento;
import com.IFPI.CLINICA.Repository.AgendamentoRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import com.IFPI.CLINICA.Model.Agendamento;

import java.time.LocalDate;
import java.util.List;

/**
 * Camada de serviço responsável pela lógica de negócio relacionada aos agendamentos da clínica.
 * Atua como intermediária entre os Controllers JavaFX e o repositório de dados JPA,
 * garantindo a integridade das operações de agendamento, cancelamento e consulta.
 */
@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;

    /**
     * Injeção de dependência via construtor para o AgendamentoRepository.
     */
    public AgendamentoService(AgendamentoRepository repository){
        this.repository = repository;
    }

    /**
     * Persiste um novo agendamento ou atualiza um existente de forma imediata.
     * Utiliza 'saveAndFlush' para garantir que as alterações sejam enviadas ao banco instantaneamente.
     * @param agendamento Objeto contendo os dados do paciente, data, hora e procedimento.
     * @return O agendamento salvo ou null em caso de falha.
     */
    public Agendamento marcarAgendamento(Agendamento agendamento) {
        try {
            return repository.saveAndFlush(agendamento);
        } catch (Exception e) {
            System.out.println("Erro ao marcar agendamento: " + e.getMessage());
            return null;
        }
    }

    /**
     * Recupera todos os registros de agendamento presentes na base de dados.
     */
    public List<Agendamento> listarAgendamentos(){
        return repository.findAll();
    }

    /**
     * Filtra agendamentos dentro de um intervalo de datas específico.
     * Útil para geração de relatórios e visualização de agenda semanal/mensal.
     * * @param inicio Data inicial do filtro.
     * @param fim Data final do filtro.
     */
    public List<Agendamento> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return repository.findByDataBetween(inicio, fim);
    }

    /**
     * Localiza um agendamento específico pelo seu identificador único.
     * Caso não encontre, captura a exceção e retorna null para evitar interrupções no fluxo da UI.
     */
    public Agendamento buscarPorId(Integer id){
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        } catch (RuntimeException e) {
            System.out.println("Erro ao buscar agendamento: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca todos os agendamentos marcados para uma data específica.
     */
    public List<Agendamento> buscarPorData(LocalDate data) {
        return repository.findByData(data);
    }

    /**
     * Realiza uma busca refinada por data e uma lista de status (ex: PENDENTE, REALIZADA).
     */
    public List<Agendamento> buscarPorDataEStatus(LocalDate data, List<StatusAgendamento> status) {
        return repository.findByDataAndStatusIn(data, status);
    }

    /**
     * Realiza o cancelamento lógico de um agendamento.
     * Localiza o registro e altera seu status para 'CANCELADA', preservando o histórico no banco.
     * * @param id Identificador do agendamento a ser cancelado.
     */
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

    /**
     * Atualiza as informações de tempo (data/hora) e status de um agendamento existente.
     * * @param id Identificador do registro original.
     * @param novoAgendamento Objeto contendo os novos dados a serem aplicados.
     */
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





