package com.example.demo.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RelatorioDTO {

    private Long id;

    @NotNull
private Long postoId;
private String nomePosto;

    private LocalDateTime dataHora;

    @NotNull
    private Integer manhaPrevencoes;
    @NotNull
    private Integer manhaAtaques;
    @NotNull
    private Integer tardePrevencoes;
    @NotNull
    private Integer tardeAtaques;

}
