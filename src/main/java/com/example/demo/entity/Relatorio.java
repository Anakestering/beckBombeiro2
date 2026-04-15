package com.example.demo.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    private Integer prevencoes;

    private Integer ataques;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Posto posto;

}