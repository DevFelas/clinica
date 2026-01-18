package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.model.Procedimento;
import com.IFPI.CLINICA.repository.ProcedimentoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProcedimentoService {

    private final ProcedimentoRepository repository;

    //injeção de dependências
    public ProcedimentoService(ProcedimentoRepository repository) {
        this.repository = repository;
    }

    //CREATE
    public void salvarProcedimento(Procedimento procedimento){
        //salva e fecha a conexão com o banco de dados
        repository.saveAndFlush(procedimento);
    }

    //READ
    public Procedimento buscarProcedimentoPorId(Integer id){
        //tratamento de exceções para quando não se encontra o id de procedimento
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("Procedimento não encontrado")
        );
    }

    //DELETE
    public void deletarProcedimentoPorId(Integer id){
        repository.deleteById(id);
    }

    //UPDATE
    public void atualizarProcedimentoPorId(Integer id, Procedimento procedimento){
        //aqui eu vou colocar o id do procedimento que quero atualizar e depois os atributos a serem atualizados
        //caso algum desses eu não tenha colocado como parâmetro, vai retornar o que já existe no banco
        //isso é importante para que não atualize algo e perca o resto
        Procedimento procedimentoEntity = buscarProcedimentoPorId(id);
        Procedimento procedimentoAtualizado = Procedimento.builder()
                .id(procedimentoEntity.getId())
                .nome(procedimento.getNome() != null ? procedimento.getNome() :
                        procedimentoEntity.getNome())
                .valor(procedimento.getValor() != null ? procedimento.getValor() :
                        procedimentoEntity.getValor())
                .tempo_previsto(procedimento.getTempo_previsto() != null ? procedimento.getTempo_previsto() :
                        procedimentoEntity.getTempo_previsto())
                .build();

        repository.saveAndFlush(procedimentoAtualizado);
    }
}
