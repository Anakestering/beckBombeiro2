package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.RelatorioDTO;
import com.example.demo.entity.Posto;
import com.example.demo.entity.Relatorio;
import com.example.demo.repository.PostoRepository;
import com.example.demo.repository.RelatorioRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;

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

    /**
     * 🔥 SOBRESCREVE O CREATE DA BASE
     * Isso garante que o fluxo use o seu toEntity customizado e salve corretamente.
     */
    @Override
    @Transactional
    public RelatorioDTO create(RelatorioDTO dto) {
        Relatorio entity = this.toEntity(dto);
        Relatorio salvo = relatorioRepository.save(entity);
        return this.toDto(salvo);
    }

    /**
     * 🔥 CONVERSÃO DTO -> ENTITY
     * Ajustado para garantir que os campos obrigatórios e o Posto sejam setados.
     */
    @Override
    public Relatorio toEntity(RelatorioDTO dto) {

        Relatorio entity = new Relatorio();

        entity.setManhaPrevencoes(dto.getManhaPrevencoes());
        entity.setManhaAtaques(dto.getManhaAtaques());
        entity.setTardePrevencoes(dto.getTardePrevencoes());
        entity.setTardeAtaques(dto.getTardeAtaques());

        if (dto.getPostoId() != null) {
            Posto posto = postoRepository.findById(dto.getPostoId())
                    .orElseThrow(() -> new RuntimeException("Posto não encontrado"));

            entity.setPosto(posto);
        }

        entity.setDataHora(LocalDateTime.now());
        entity.setAtivo(true);

        return entity;
    }

    /**
     * 🔥 CONVERSÃO ENTITY -> DTO
     * Garante que o Front-end receba o nome do posto e o ID corretamente.
     */
    @Override
    public RelatorioDTO toDto(Relatorio entity) {

        RelatorioDTO dto = new RelatorioDTO();

        dto.setId(entity.getId());
        dto.setDataHora(entity.getDataHora());

        dto.setManhaPrevencoes(entity.getManhaPrevencoes());
        dto.setManhaAtaques(entity.getManhaAtaques());
        dto.setTardePrevencoes(entity.getTardePrevencoes());
        dto.setTardeAtaques(entity.getTardeAtaques());

        if (entity.getPosto() != null) {
            dto.setPostoId(entity.getPosto().getId());
            dto.setNomePosto(entity.getPosto().getNome());
        }

        return dto;
    }

    /**
     * 🔥 LISTAR TODOS
     * Filtra apenas os ativos (seguindo a lógica do seu soft delete na Base)
     */
    public List<RelatorioDTO> listarTodos() {
        return relatorioRepository.findAll()
                .stream()
                .filter(Relatorio::isVisivelAdmin)
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void ocultarTodos() {
        List<Relatorio> lista = relatorioRepository.findAll();

        for (Relatorio r : lista) {
            r.setVisivelAdmin(false);
        }

        relatorioRepository.saveAll(lista);
    }

    @Transactional
    public void ocultar(Long id) {
        Relatorio r = relatorioRepository.findById(id)
                .orElseThrow();

        r.setVisivelAdmin(false);
        relatorioRepository.save(r);
    }

    public RelatorioDTO buscarPorPostoId(Long postoId) {
        return relatorioRepository
                .findTopByPostoIdOrderByDataHoraDesc(postoId)
                .map(this::toDto)
                .orElse(null);
    }

    /**
     * 🔥 EXPORTAÇÃO EXCEL
     */

    public void exportExcelPorPeriodo(String inicio, String fim, HttpServletResponse response) throws Exception {

        LocalDateTime dataInicio = LocalDateTime.parse(inicio);
        LocalDateTime dataFim = LocalDateTime.parse(fim);

        List<Relatorio> relatorios = relatorioRepository
                .findByDataHoraBetween(dataInicio, dataFim);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatórios");

            Row header = sheet.createRow(0);
            String[] colunas = {
                    "Posto",
                    "Manhã Prev", "Manhã Ataques",
                    "Tarde Prev", "Tarde Ataques",
                    "Data/Hora"
            };

            for (int i = 0; i < colunas.length; i++) {
                header.createCell(i).setCellValue(colunas[i]);
            }

            int rowNum = 1;
            for (Relatorio r : relatorios) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(r.getPosto().getNome());
                row.createCell(1).setCellValue(r.getManhaPrevencoes());
                row.createCell(2).setCellValue(r.getManhaAtaques());
                row.createCell(3).setCellValue(r.getTardePrevencoes());
                row.createCell(4).setCellValue(r.getTardeAtaques());
                row.createCell(5).setCellValue(r.getDataHora().toString());
            }

            workbook.write(response.getOutputStream());
        }
    }
}
