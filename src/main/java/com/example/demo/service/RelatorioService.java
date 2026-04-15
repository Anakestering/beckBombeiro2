package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.RelatorioDTO;
import com.example.demo.entity.Posto;
import com.example.demo.entity.Relatorio;
import com.example.demo.repository.PostoRepository;
import com.example.demo.repository.RelatorioRepository;

@Service
public class RelatorioService extends BaseService<Relatorio, RelatorioDTO> {

    private final RelatorioRepository relatorioRepository;
    private final PostoRepository postoRepository;

    public RelatorioService(RelatorioRepository relatorioRepository,
                            PostoRepository postoRepository) {
        super(relatorioRepository);
        this.relatorioRepository = relatorioRepository;
        this.postoRepository = postoRepository;
    }

    // 🔥 Sobrescreve conversão pra incluir nome do posto
    @Override
    public RelatorioDTO toDto(Relatorio entity) {
        RelatorioDTO dto = super.toDto(entity);

        if (entity.getPosto() != null) {
            dto.setPostoId(entity.getPosto().getId());
            dto.setNomePosto(entity.getPosto().getNome());
        }

        return dto;
    }

    // 🔥 Sobrescreve pra conseguir salvar com posto
    @Override
    public Relatorio toEntity(RelatorioDTO dto) {
        Relatorio entity = super.toEntity(dto);

        if (dto.getPostoId() != null) {
            Posto posto = postoRepository.findById(dto.getPostoId())
                    .orElseThrow(() -> new RuntimeException("Posto não encontrado"));

            entity.setPosto(posto);
        }

         entity.setDataHora(LocalDateTime.now());

        return entity;
    }

    // 🔥 Método útil pro admin (listar tudo com DTO)
    public List<RelatorioDTO> listarTodos() {
        return relatorioRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }
}