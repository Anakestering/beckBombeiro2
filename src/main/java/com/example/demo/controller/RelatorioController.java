package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.RelatorioDTO;
import com.example.demo.service.RelatorioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    // 🔥 Criar relatório (usado no Dashboard)
    @PostMapping
    public RelatorioDTO criar(@RequestBody RelatorioDTO dto) {
        return relatorioService.create(dto);
    }

    // 🔥 Listar todos (admin)
    @GetMapping
    public List<RelatorioDTO> listar() {
        return relatorioService.listarTodos();
    }

    // 🔥 Buscar por ID (caso precise)
    @GetMapping("/{id}")
    public RelatorioDTO buscarPorId(@PathVariable Long id) {
        return relatorioService.read(id);
    }

    // 🔥 Atualizar (se quiser editar depois)
    @PutMapping("/{id}")
    public RelatorioDTO atualizar(@PathVariable Long id, @RequestBody RelatorioDTO dto) {
        return relatorioService.update(id, dto);
    }

    // 🔥 Soft delete (remover da tela, não do banco)
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        relatorioService.softDelete(id);
    }
}