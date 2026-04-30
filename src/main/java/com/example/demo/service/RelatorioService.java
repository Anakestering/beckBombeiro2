package com.example.demo.service;

import java.time.LocalDate;
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

        
        Posto posto = postoRepository.findById(dto.getPostoId())
                .orElseThrow(() -> new RuntimeException("Posto não encontrado"));

    
        LocalDate data = LocalDate.now();

        
        var existente = relatorioRepository.findByPostoAndData(posto, data);

        Relatorio relatorio;

        if (existente.isPresent()) {
          
            relatorio = existente.get();

        } else {
          
            relatorio = new Relatorio();
            relatorio.setPosto(posto);
            relatorio.setData(data);
        }

      
        relatorio.setDataHora(LocalDateTime.now());
      
        relatorio.setManhaPrevencoes(dto.getManhaPrevencoes());
        relatorio.setManhaAtaques(dto.getManhaAtaques());
        relatorio.setTardePrevencoes(dto.getTardePrevencoes());
        relatorio.setTardeAtaques(dto.getTardeAtaques());

        Relatorio salvo = relatorioRepository.save(relatorio);

        return toDto(salvo);
    }

    
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
        dto.setOrdemPosto(entity.getPosto().getOrdem());

        return dto;
    }

    
    public List<RelatorioDTO> listarTodos() {
        return relatorioRepository.buscarOrdenados()
                .stream()
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

    public RelatorioDTO buscarHoje(Long postoId) {

    Posto posto = postoRepository.findById(postoId)
        .orElseThrow();

    LocalDate hoje = LocalDate.now();

    return relatorioRepository
        .findByPostoAndData(posto, hoje)
        .map(this::toDto)
        .orElse(null);
}

    
    public void exportExcelPorPeriodo(String inicio, String fim, HttpServletResponse response) throws Exception {

        LocalDateTime dataInicio = LocalDateTime.parse(inicio);
        LocalDateTime dataFim = LocalDateTime.parse(fim);

        List<Relatorio> relatorios = relatorioRepository
                .findByDataHoraBetween(dataInicio, dataFim);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatórios");

            // =========================
            // ESTILO DATA
            // =========================
            CreationHelper creationHelper = workbook.getCreationHelper();
            CellStyle dataStylePar = workbook.createCellStyle();
            CellStyle dataStyleImpar = workbook.createCellStyle();

            short formatoData = creationHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm");

            // =========================
            // ESTILO HEADER
            // =========================
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            headerStyle.setFont(headerFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // =========================
            // ESTILO LINHAS (ZEBRADO)
            // =========================
            CellStyle stylePar = workbook.createCellStyle();
            CellStyle styleImpar = workbook.createCellStyle();

            for (CellStyle style : new CellStyle[] { stylePar, styleImpar, dataStylePar, dataStyleImpar }) {
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
            }

            // cores
            stylePar.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            stylePar.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            styleImpar.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            styleImpar.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            dataStylePar.cloneStyleFrom(stylePar);
            dataStylePar.setDataFormat(formatoData);

            dataStyleImpar.cloneStyleFrom(styleImpar);
            dataStyleImpar.setDataFormat(formatoData);

            // =========================
            // HEADER
            // =========================
            Row header = sheet.createRow(0);
            String[] colunas = {
                    "Posto",
                    "Manhã Prevenções", "Manhã Lesões água-viva",
                    "Tarde Prevenções", "Tarde Lesões água-viva",
                    "Data/Hora"
            };

            for (int i = 0; i < colunas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            sheet.createFreezePane(0, 1);

            // =========================
            // DADOS
            // =========================
            int rowNum = 1;

            for (Relatorio r : relatorios) {
                Row row = sheet.createRow(rowNum);

                boolean isPar = rowNum % 2 == 0;

                CellStyle linhaStyle = isPar ? stylePar : styleImpar;
                CellStyle dataStyle = isPar ? dataStylePar : dataStyleImpar;

                // Criar células com estilo
                for (int col = 0; col <= 5; col++) {
                    Cell cell = row.createCell(col);
                    cell.setCellStyle(linhaStyle);
                }

                row.getCell(0).setCellValue(r.getPosto().getNome());
                row.getCell(1).setCellValue(r.getManhaPrevencoes());
                row.getCell(2).setCellValue(r.getManhaAtaques());
                row.getCell(3).setCellValue(r.getTardePrevencoes());
                row.getCell(4).setCellValue(r.getTardeAtaques());

                // DATA CORRETA
                Cell cellData = row.getCell(5);
                cellData.setCellValue(java.sql.Timestamp.valueOf(r.getDataHora()));
                cellData.setCellStyle(dataStyle);

                rowNum++;
            }

            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}