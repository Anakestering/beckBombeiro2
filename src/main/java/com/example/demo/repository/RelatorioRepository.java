package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Posto;
import com.example.demo.entity.Relatorio;

@Repository
public interface RelatorioRepository extends BaseRepository<Relatorio, Long> {

    // Buscar todos os relatórios de um posto
    List<Relatorio> findByPosto(Posto posto);

    // Buscar relatórios por período
    List<Relatorio> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar por posto + período (muito útil pro admin)
    List<Relatorio> findByPostoAndDataHoraBetween(
        Posto posto,
        LocalDateTime inicio,
        LocalDateTime fim
    );

}