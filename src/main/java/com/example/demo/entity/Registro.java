package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.enums.TipoRegistro;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Registro extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private TipoRegistro tipo;

    @Column(length = 500)
    private String urlImagem;

    @Column(nullable = false)
    private boolean visivelAdmin = true;

    private LocalDateTime dataHora;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Posto posto;

}