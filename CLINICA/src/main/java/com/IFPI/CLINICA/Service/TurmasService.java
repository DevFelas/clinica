package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Turmas;
import com.IFPI.CLINICA.Repository.TurmasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurmasService {

    @Autowired
    private TurmasRepository repository;

    public List<Turmas> listar() {
        return repository.findAll();
    }

    public Turmas cadastrar(Turmas turma) {
        return repository.save(turma);
    }

    public Turmas buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void remover(Integer id) {
        repository.deleteById(id);
    }
}
