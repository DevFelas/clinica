package com.IFPI.CLINICA.controller;

import com.IFPI.CLINICA.Service.ProcedimentoService;
import com.IFPI.CLINICA.model.Procedimento;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/procedimento")
@RequiredArgsConstructor //Isso permite injeção de dependência sem @Autowired criando um construtor automaticamente
public class ProcedimentoController {

    private final ProcedimentoService procedimentoService;

    @PostMapping
    public ResponseEntity<Void> salvarProcedimento(@RequestBody Procedimento procedimento){
        procedimentoService.salvarProcedimento(procedimento);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Procedimento> buscarProcedimentoPorId(@RequestParam Integer id){
        return new ResponseEntity.ok(ProcedimentoService.buscarProcedimentoPorId(id));
    }

}
