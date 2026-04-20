package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Posto;
import com.example.demo.entity.Registro;
import com.example.demo.enums.TipoRegistro;

@Repository
public interface RegistroRepository extends BaseRepository<Registro, Long> {

        // Buscar todos registros de um posto
        List<Registro> findByPosto(Posto posto);

        // Buscar registros de um posto por tipo
        List<Registro> findByPostoAndTipo(Posto posto, TipoRegistro tipo);

        // Buscar por período (muito importante)
        List<Registro> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

        // Buscar completo (posto + tipo + período)
        List<Registro> findByPostoIdAndTipoAndDataHoraBetween(
                        Long postoId,
                        TipoRegistro tipo,
                        LocalDateTime inicio,
                        LocalDateTime fim);

        List<Registro> findByPostoIdAndDataHoraBetween(
                        Long postoId,
                        LocalDateTime inicio,
                        LocalDateTime fim);

        @Query("""
                            SELECT r FROM Registro r
                            JOIN r.posto p
                            WHERE r.visivelAdmin = true
                            ORDER BY
                                CASE WHEN p.ordem IS NULL THEN 1 ELSE 0 END,
                                p.ordem ASC,
                                r.dataHora ASC
                        """)
        List<Registro> buscarOrdenadosPorPosto();
}