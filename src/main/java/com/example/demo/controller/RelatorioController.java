package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.RelatorioDTO;
import com.example.demo.service.RelatorioService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/export")
    public void exportExcelPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=relatorios.xlsx");

        relatorioService.exportExcelPorPeriodo(inicio, fim, response);
    }


    @PostMapping
    public RelatorioDTO criar(@RequestBody RelatorioDTO dto) {
        return relatorioService.create(dto);
    }

   
    @GetMapping
    public List<RelatorioDTO> listar() {
        return relatorioService.listarTodos();
    }

    
    @GetMapping("/id/{id}")
    public RelatorioDTO buscarPorId(@PathVariable Long id) {
        return relatorioService.read(id);
    }

   
    @PutMapping("/{id}")
    public RelatorioDTO atualizar(@PathVariable Long id, @RequestBody RelatorioDTO dto) {
        return relatorioService.update(id, dto);
    }

    @GetMapping("/posto/{postoId}")
    public RelatorioDTO buscarPorPosto(@PathVariable Long postoId) {
        return relatorioService.buscarPorPostoId(postoId);
    }

    
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        relatorioService.softDelete(id);
    }

    @PatchMapping("/ocultar-todos")
    public void ocultarTodos() {
        relatorioService.ocultarTodos();
    }

    @PatchMapping("/ocultar/{id}")
    public void ocultar(@PathVariable Long id) {
        relatorioService.ocultar(id);
    }

    @GetMapping("/hoje/{postoId}")
public RelatorioDTO buscarHoje(@PathVariable Long postoId) {
    return relatorioService.buscarHoje(postoId);
}

}