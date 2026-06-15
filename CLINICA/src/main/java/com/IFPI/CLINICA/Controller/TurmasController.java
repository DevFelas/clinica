package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Turmas;
import com.IFPI.CLINICA.Service.TurmasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmasController {

    @Autowired
    private TurmasService service;

    @GetMapping
    public List<Turmas> listar() {
        return service.listar();
    }

    @PostMapping
    public Turmas cadastrar(@RequestBody Turmas turma) {
        return service.cadastrar(turma);
    }

    @GetMapping("/{id}")
    public Turmas buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Integer id) {
        service.remover(id);
    }
}
