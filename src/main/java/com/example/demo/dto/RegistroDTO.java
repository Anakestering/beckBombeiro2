package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RegistroDTO {

    private Long id;
    private String urlImagem;
    private LocalDateTime dataHora;
    private String tipo; // CHECKIN ou CHECKOUT

    private Long postoId;
    private String nomePosto;

}