package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Service.ProcedimentoService;
import com.IFPI.CLINICA.Model.Procedimento;
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
        return  ResponseEntity.ok(procedimentoService.buscarProcedimentoPorId(id));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarProcedimentoPorId(@RequestParam Integer id){
        procedimentoService.deletarProcedimentoPorId(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> atualizarProcedimentoPorId(@RequestParam Integer id,
                                                           @RequestBody Procedimento procedimento){
        procedimentoService.atualizarProcedimentoPorId(id, procedimento);
        return ResponseEntity.ok().build();
    }

}
