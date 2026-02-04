package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Repository.PacienteRepository;
import com.IFPI.CLINICA.Repository.ProcedimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public Paciente cadastrar(Paciente paciente) {
        return repository.save(paciente);
    }

    public List<Paciente> listar() {
        return repository.findAll();
    }

    public Paciente buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public Optional<Paciente> buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    public Paciente atualizar(Integer id, Paciente dados) {
        Paciente paciente = buscarPorId(id);
        paciente.setNome(dados.getNome());
        paciente.setContato(dados.getContato());
        return repository.save(paciente);
    }

    public void remover(Integer id) {
        repository.deleteById(id);
    }

    public List<Paciente> listarPacientes() { return repository.findAll(); }
}
