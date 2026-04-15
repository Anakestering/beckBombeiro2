package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RelatorioDTO {

    private Long id;
    private LocalDateTime dataHora;
    private Integer prevencoes;
    private Integer ataques;

    private Long postoId;
    private String nomePosto;

}