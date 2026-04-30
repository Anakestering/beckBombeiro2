package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.RegistroDTO;
import com.example.demo.service.RegistroService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/registros")
public class RegistroController extends BaseController<RegistroDTO> {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        super(registroService);
        this.registroService = registroService;
    }

    
    @Override
    @PostMapping
    public RegistroDTO create(@RequestBody RegistroDTO dto) {
        return registroService.criarRegistro(dto);
    }

   
    @Override
    @GetMapping
    public List<RegistroDTO> read() {
        return registroService.listarTodos();
    }

    @PatchMapping("/ocultar-todos")
    public void ocultarTodos() {
        registroService.ocultarTodos();
    }

    @PatchMapping("/ocultar/{id}")
    public void ocultar(@PathVariable Long id) {
        registroService.ocultar(id);
    }

    @GetMapping("/hoje/{postoId}")
    public List<RegistroDTO> buscarHoje(@PathVariable Long postoId) {
        return registroService.buscarHoje(postoId);
    }

    

}