package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Paciente;
import com.IFPI.CLINICA.Repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public Paciente cadastrar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    public Paciente buscarPorId(Integer id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public Paciente atualizar(Integer id, Paciente dados) {
        Paciente paciente = buscarPorId(id);
        paciente.setNome(dados.getNome());
        paciente.setContato(dados.getContato());
        return pacienteRepository.save(paciente);
    }

    public void remover(Integer id) {
        pacienteRepository.deleteById(id);
    }

}
