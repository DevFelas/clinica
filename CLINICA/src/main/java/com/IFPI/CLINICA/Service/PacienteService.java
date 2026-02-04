package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Repository.PacienteRepository;
import com.IFPI.CLINICA.Repository.ProcedimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Especialização de componente gerenciada pelo Spring Framework.
 * A anotação @Service indica que esta classe contém a lógica de domínio
 * e coordena as operações da entidade Paciente.
 */
@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    /**
     * Método construtor para viabilizar a injeção de dependência via parâmetro.
     * @param repository Repositório de abstração da persistência de dados.
     */
    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    /**
     * Realiza a persistência de uma nova instância da entidade Paciente no banco de dados.
     * @param paciente Objeto contendo os atributos a serem registrados.
     * @return O objeto persistido, devidamente sincronizado com o estado do banco.
     */
    public Paciente cadastrar(Paciente paciente) {
        return repository.save(paciente);
    }

    /**
     * Recupera a coleção integral de registros de pacientes armazenados.
     * @return Uma lista tipada contendo todos os objetos da entidade encontrados.
     */
    public List<Paciente> listar() {
        return repository.findAll();
    }

    /**
     * Executa a busca de um registro específico baseado em seu identificador primário.
     * Implementa o tratamento de ausência de registro através do lançamento de exceção em tempo de execução.
     * @param id Chave primária do registro.
     * @return Instância de Paciente recuperada.
     * @throws RuntimeException Caso a busca não retorne resultados.
     */
    public Paciente buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    /**
     * Disponibiliza um mecanismo de busca baseado em atributo único (CPF), utilizando o container Optional
     * Para reduzir o risco de exceções de referência nula (NullPointerException).
     * @param cpf Documento de identificação único do paciente.
     * @return Um container Optional que pode ou não conter a instância de Paciente.
     */
    public Optional<Paciente> buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    /**
     * Atualiza o estado de um registro existente. O método primeiro valida a existência
     * do registro e, subsequentemente, sobrescreve os atributos mutáveis (nome e contato).
     * @param id Identificador do registro a ser modificado.
     * @param dados Objeto contendo os novos estados da entidade.
     * @return Instância de Paciente após a sincronização da alteração.
     */
    public Paciente atualizar(Integer id, Paciente dados) {
        Paciente paciente = buscarPorId(id);
        paciente.setNome(dados.getNome());
        paciente.setContato(dados.getContato());
        return repository.save(paciente);
    }


    /**
     * Exclui permanentemente o registro correspondente ao identificador fornecido do banco de dados.
     * @param id Chave primária do registro a ser removido.
     */
    public void remover(Integer id) {
        repository.deleteById(id);
    }

    /**
     * Método redundante para recuperação de todas as instâncias da entidade.
     * @return Listagem completa de pacientes.
     */
    public List<Paciente> listarPacientes() { return repository.findAll(); }
}
