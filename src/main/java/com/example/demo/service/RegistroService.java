package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.RegistroDTO;
import com.example.demo.dto.RelatorioDTO;
import com.example.demo.entity.Posto;
import com.example.demo.entity.Registro;
import com.example.demo.entity.Relatorio;
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

    // 🔥 MÉTODO PRINCIPAL
    public RegistroDTO criarRegistro(RegistroDTO dto) {

        Posto posto = postoRepository.findById(dto.getPostoId())
                .orElseThrow(() -> new RuntimeException("Posto não encontrado"));

        TipoRegistro tipo = TipoRegistro.valueOf(dto.getTipo());

        // ================= REGRAS DE NEGÓCIO =================
        if (tipo == TipoRegistro.CHECKOUT) {

            boolean temCheckinHoje = registroRepository
                    .findByPostoAndTipo(posto, TipoRegistro.CHECKIN)
                    .stream()
                    .anyMatch(r -> isHoje(r.getDataHora()));

            if (!temCheckinHoje) {
                throw new RuntimeException("Precisa fazer check-in antes");
            }

            boolean temRelatorioHoje = relatorioRepository
                    .findByPosto(posto)
                    .stream()
                    .anyMatch(r -> isHoje(r.getDataHora()));

            if (!temRelatorioHoje) {
                throw new RuntimeException("Precisa preencher o relatório antes");
            }
        }

        // ================= 🔥 SALVAR IMAGEM =================
        try {
            String base64 = dto.getUrlImagem();

            // remove prefixo (data:image/jpeg;base64,)
            String base64Limpo = base64.split(",")[1];

            byte[] imagemBytes = Base64.getDecoder().decode(base64Limpo);

            String nomeArquivo = UUID.randomUUID() + ".jpg";

            Path caminho = Paths.get("uploads/" + nomeArquivo);

            Files.createDirectories(caminho.getParent());
            Files.write(caminho, imagemBytes);

            // 🔥 substituir base64 por URL
            String url = "http://localhost:8080/uploads/" + nomeArquivo;
            dto.setUrlImagem(url);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem");
        }

        // ================= SALVAR NO BANCO =================
        RegistroDTO salvo = super.create(dto);

        // ================= AJUSTES FINAIS =================
        Registro entity = registroRepository.findById(salvo.getId()).orElseThrow();

        entity.setPosto(posto);
        entity.setTipo(tipo);
        entity.setDataHora(LocalDateTime.now());

        registroRepository.save(entity);

        return toDto(entity);
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

    // 🔥 listar (admin)
    public List<RegistroDTO> listarTodos() {
        return registroRepository.findAll()
                .stream()
                .filter(Registro::isVisivelAdmin)
                .map(this::toDto)
                .toList();
    }

    // 🔧 auxiliar
    private boolean isHoje(LocalDateTime data) {
        return data.toLocalDate().equals(LocalDate.now());
    }

    

    // Ocultores

    @Transactional
    public void ocultarTodos() {
        List<Registro> lista = registroRepository.findAll();

        for (Registro r : lista) {
            r.setVisivelAdmin(false);
        }

        registroRepository.saveAll(lista);
    }

    @Transactional
    public void ocultar(Long id) {
        Registro r = registroRepository.findById(id)
                .orElseThrow();

        r.setVisivelAdmin(false);
        registroRepository.save(r);
    }
}