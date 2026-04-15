package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Posto;
import com.example.demo.entity.Registro;
import com.example.demo.enums.TipoRegistro;

@Repository
public interface RegistroRepository extends BaseRepository<Registro, Long> {

    // Buscar todos registros de um posto
    List<Registro> findByPosto(Posto posto);

    // Buscar por tipo (CHECKIN ou CHECKOUT)
    List<Registro> findByTipo(TipoRegistro tipo);

    // Buscar registros de um posto por tipo
    List<Registro> findByPostoAndTipo(Posto posto, TipoRegistro tipo);

    // Buscar por período (muito importante)
    List<Registro> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar completo (posto + tipo + período)
    List<Registro> findByPostoAndTipoAndDataHoraBetween(
        Posto posto,
        TipoRegistro tipo,
        LocalDateTime inicio,
        LocalDateTime fim
    );

}