package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.RegistroDTO;
import com.example.demo.entity.Posto;
import com.example.demo.entity.Registro;
import com.example.demo.enums.TipoRegistro;
import com.example.demo.repository.PostoRepository;
import com.example.demo.repository.RegistroRepository;
import com.example.demo.repository.RelatorioRepository;

@Service
public class RegistroService extends BaseService<Registro, RegistroDTO> {

    private final RegistroRepository registroRepository;
    private final PostoRepository postoRepository;
    private final RelatorioRepository relatorioRepository;

    public RegistroService(RegistroRepository registroRepository,
            PostoRepository postoRepository,
            RelatorioRepository relatorioRepository) {
        super(registroRepository);
        this.registroRepository = registroRepository;
        this.postoRepository = postoRepository;
        this.relatorioRepository = relatorioRepository;
    }

    // 🔥 MÉTODO PRINCIPAL (usar no controller)
    public RegistroDTO criarRegistro(RegistroDTO dto) {

        Posto posto = postoRepository.findById(dto.getPostoId())
                .orElseThrow(() -> new RuntimeException("Posto não encontrado"));

        TipoRegistro tipo = TipoRegistro.valueOf(dto.getTipo());

        // 🔴 REGRA DE NEGÓCIO: CHECKOUT
        if (tipo == TipoRegistro.CHECKOUT) {

            // ✔️ verificar se teve CHECKIN hoje
            boolean temCheckinHoje = registroRepository
                    .findByPostoAndTipo(posto, TipoRegistro.CHECKIN)
                    .stream()
                    .anyMatch(r -> isHoje(r.getDataHora()));

            if (!temCheckinHoje) {
                throw new RuntimeException("Precisa fazer check-in antes");
            }

            // ✔️ verificar se tem relatório hoje
            boolean temRelatorioHoje = relatorioRepository
                    .findByPosto(posto)
                    .stream()
                    .anyMatch(r -> isHoje(r.getDataHora()));

            if (!temRelatorioHoje) {
                throw new RuntimeException("Precisa preencher o relatório antes");
            }
        }

        // salvar usando BaseService
        return super.create(dto);
    }

    // 🔥 Converter Entity → DTO
    @Override
    public RegistroDTO toDto(Registro entity) {
        RegistroDTO dto = super.toDto(entity);

        if (entity.getPosto() != null) {
            dto.setPostoId(entity.getPosto().getId());
            dto.setNomePosto(entity.getPosto().getNome());
        }

        if (entity.getTipo() != null) {
            dto.setTipo(entity.getTipo().name());
        }

        return dto;
    }

    // 🔥 listar para admin (fotos)
    public List<RegistroDTO> listarTodos() {
        return registroRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 🔧 auxiliar (verificar se é hoje)
    private boolean isHoje(LocalDateTime data) {
        return data.toLocalDate().equals(LocalDate.now());
    }
}