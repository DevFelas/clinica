package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Procedimento;
import com.IFPI.CLINICA.Repository.ProcedimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Classe de camada de serviço responsável por gerenciar a lógica de negócio
 * referente aos procedimentos da clínica. Esta classe coordena a comunicação
 * entre a interface do sistema e o repositório de dados.
 */
@Service
public class ProcedimentoService {

    private final ProcedimentoRepository repository;

    /**
     * Classe de camada de serviço responsável por gerenciar a lógica de negócio
     * referente aos procedimentos da clínica. Esta classe coordena a comunicação
     * entre a interface do sistema e o repositório de dados.
     */
    public ProcedimentoService(ProcedimentoRepository repository) {
        this.repository = repository;
    }

    /**
     * Sincroniza os dados de um novo Procedimento com o banco de dados, garantindo sua disponibilidade futura..
     * O método assegura que a informação seja gravada e a sessão seja atualizada imediatamente.
     */
    public void salvarProcedimento(Procedimento procedimento){
        //salva e fecha a conexão com o banco de dados
        repository.saveAndFlush(procedimento);
    }

    /**
     * Busca um procedimento específico através do seu identificador único.
     * Caso o registro não seja localizado, uma interrupção é gerada através de uma exceção.
     * @param id Identificador numérico do procedimento.
     * @return O objeto Procedimento correspondente ao ID.
     */
    public Procedimento buscarProcedimentoPorId(Integer id){
        //tratamento de exceções para quando não se encontra o id de procedimento
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("Procedimento não encontrado")
        );
    }

    /**
     * Remove o registro de um procedimento da base de dados utilizando seu identificador.
     * @param id Identificador do registro a ser excluído.
     */
    public void deletarProcedimentoPorId(Integer id){
        repository.deleteById(id);
    }

    /**
     * Atualiza os dados de um procedimento existente. O método aplica uma lógica de
     * verificação que mantém os valores antigos caso novos valores não sejam fornecidos,
     * evitando a perda acidental de informações.
     * @param id Código identificador do procedimento alvo.
     * @param dados Objeto contendo os novos dados para atualização.
     */
    public Procedimento atualizarProcedimentoPorId(Integer id, Procedimento dados) {
        Procedimento p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado: " + id));

        p.setNome(dados.getNome());
        p.setValor(dados.getValor());
        p.setTempo_previsto(dados.getTempo_previsto());
        p.setCorHex(dados.getCorHex());

        garantirDefaults(p);

        return repository.save(p);
    }

    /**
     * Retorna uma lista contendo todos os procedimentos cadastrados no sistema.
     * @return Coleção de objetos do tipo Procedimento.
     */
    public List<Procedimento> listarProcedimentos() {
        return repository.findAll();
    }

    /**
     * Garante que o campo corHex não seja interpretado como null
     * @param p
     */
    private void garantirDefaults(Procedimento p) {
        if (p.getCorHex() == null || p.getCorHex().trim().isEmpty()) {
            p.setCorHex("#09c6d9");
        }
    }

}
