package com.example.demo.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Relatorio extends BaseEntity {

    private LocalDateTime dataHora;

    @Column(nullable = false)
    private boolean visivelAdmin = true;
    @Column(nullable = false)
    private Integer manhaPrevencoes;
    @Column(nullable = false)
    private Integer manhaAtaques;
    @Column(nullable = false)
    private Integer tardePrevencoes;
    @Column(nullable = false)
    private Integer tardeAtaques;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Posto posto;

}