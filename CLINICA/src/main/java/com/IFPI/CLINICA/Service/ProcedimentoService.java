package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Procedimento;
import com.IFPI.CLINICA.Repository.ProcedimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Procedimento> listarProcedimentos() {
        return repository.findAll();
    }

    private void garantirDefaults(Procedimento p) {
        if (p.getCorHex() == null || p.getCorHex().trim().isEmpty()) {
            p.setCorHex("#09c6d9");
        }
    }

}
