package com.IFPI.CLINICA.Controller;


import com.IFPI.CLINICA.Service.AgendamentoService;
import com.IFPI.CLINICA.Model.Agendamento;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoControler {

    private final AgendamentoService service;

    public AgendamentoControler(AgendamentoService service){
        this.service = service;
    }

    @GetMapping
    public List<Agendamento> listar(){
        return service.listarAgendamentos();
    }

    @GetMapping("/{id}")
    public Agendamento buscarPorId(@PathVariable Integer id){
        return service.buscarPorId(id);
    }

    @PutMapping("/cancelar/{id}")
    public Agendamento cancelar(@PathVariable Integer id){
        return service.cancelarAgendamento(id);
    }

    @PutMapping("/{id}")
    public Agendamento alterar(
            @PathVariable Integer id,
            @RequestBody Agendamento agendamento
    ){
        return service.alterarAgendamento(id, agendamento);
    }


}
