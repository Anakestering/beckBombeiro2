package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.RelatorioDTO;
import com.example.demo.entity.Posto;
import com.example.demo.entity.Relatorio;
import com.example.demo.repository.PostoRepository;
import com.example.demo.repository.RelatorioRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.ServletOutputStream;
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

    public void exportExcel(HttpServletResponse response) throws Exception {

    List<Relatorio> relatorios = relatorioRepository.findAll();

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Relatórios");

    // 🔥 Cabeçalho
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("ID");
    header.createCell(1).setCellValue("Posto");
    header.createCell(2).setCellValue("Prevencoes");
    header.createCell(3).setCellValue("Ataques");
    header.createCell(4).setCellValue("Data/Hora");

    int rowNum = 1;

    for (Relatorio r : relatorios) {
        Row row = sheet.createRow(rowNum++);

        row.createCell(0).setCellValue(r.getId());

        row.createCell(1).setCellValue(
            r.getPosto() != null ? r.getPosto().getNome() : ""
        );

        row.createCell(2).setCellValue(
            r.getPrevencoes() != null ? r.getPrevencoes() : 0
        );

        row.createCell(3).setCellValue(
            r.getAtaques() != null ? r.getAtaques() : 0
        );

        row.createCell(4).setCellValue(
            r.getDataHora() != null ? r.getDataHora().toString() : ""
        );
    }

    // 🔥 auto-size colunas
    for (int i = 0; i < 5; i++) {
        sheet.autoSizeColumn(i);
    }

    ServletOutputStream outputStream = response.getOutputStream();
    workbook.write(outputStream);

    workbook.close();
    outputStream.close();
}
}