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

    @Transactional
    public RegistroDTO criarRegistro(RegistroDTO dto) {

        Posto posto = postoRepository.findById(dto.getPostoId())
                .orElseThrow(() -> new RuntimeException("Posto não encontrado"));

        TipoRegistro tipo;

        try {
            tipo = TipoRegistro.valueOf(dto.getTipo().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Tipo inválido");
        }

        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.atTime(23, 59, 59);

        // ================= 🔥 LIMITE DE 3 POR DIA =================

        List<Registro> registrosHoje = registroRepository
                .findByPostoIdAndTipoAndDataHoraBetween(posto.getId(), tipo, inicio, fim);

        boolean temCheckinHoje = !registroRepository
                .findByPostoIdAndTipoAndDataHoraBetween(posto.getId(), TipoRegistro.CHECKIN, inicio, fim)
                .isEmpty();

        if (registrosHoje.size() >= 3) {
            throw new RuntimeException("Limite de 3 registros por dia atingido");
        }

        // ================= REGRAS CHECKOUT =================
        if (tipo == TipoRegistro.CHECKOUT) {

            if (!temCheckinHoje) {
                throw new RuntimeException("Precisa fazer check-in antes");
            }

            boolean temRelatorioHoje = relatorioRepository
                    .findByPostoAndData(posto, hoje)
                    .isPresent();

            if (!temRelatorioHoje) {
                throw new RuntimeException("Precisa preencher o relatório antes");
            }
        }

        // ================= 🔥 SALVAR IMAGEM =================
        String urlImagem;

        try {
            String base64 = dto.getUrlImagem();

            String[] partes = base64.split(",");
            if (partes.length < 2) {
                throw new RuntimeException("Imagem inválida");
            }

            String base64Limpo = partes[1];
            byte[] imagemBytes = Base64.getDecoder().decode(base64Limpo);

            String nomeArquivo = UUID.randomUUID() + ".jpg";
            Path caminho = Paths.get("uploads/" + nomeArquivo);

            Files.createDirectories(caminho.getParent());
            Files.write(caminho, imagemBytes);

            urlImagem = "http://localhost:8080/uploads/" + nomeArquivo;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem");
        }

        // ================= 🔥 SALVAR DIRETO (SEM GAMBIARRA) =================
        Registro entity = new Registro();

        entity.setPosto(posto);
        entity.setTipo(tipo);
        entity.setDataHora(LocalDateTime.now());
        entity.setUrlImagem(urlImagem);
        entity.setAtivo(true);

        Registro salvo = registroRepository.save(entity);

        return toDto(salvo);
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

    public List<RegistroDTO> buscarHoje(Long postoId) {

        LocalDate hoje = LocalDate.now();

        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.atTime(23, 59, 59);

        return registroRepository
                .findByPostoIdAndDataHoraBetween(postoId, inicio, fim)
                .stream()
                .map(this::toDto)
                .toList();
    }

}