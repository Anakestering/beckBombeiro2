package com.example.demo.entity;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)

public class Posto extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer ordem;

    @JsonIgnore
    @OneToMany(mappedBy = "posto", fetch = FetchType.LAZY)
    private List<Relatorio> relatorios;

    @JsonIgnore
    @OneToMany(mappedBy = "posto", fetch = FetchType.LAZY)
    private List<Registro> registros;

}