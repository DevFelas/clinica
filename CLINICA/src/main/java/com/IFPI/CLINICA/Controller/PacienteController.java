package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Service.PacienteService;
import com.IFPI.CLINICA.Model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    @GetMapping
    public List<Paciente> listar() {
        return service.listar();
    }

    @PostMapping
    public Paciente cadastrar(@RequestBody Paciente paciente) {
        return service.cadastrar(paciente);
    }

    @GetMapping("/{id}")
    public Paciente buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Integer id) {
        service.remover(id);
    }

}
