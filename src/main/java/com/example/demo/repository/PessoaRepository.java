package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Pessoa;

@Repository
public interface PessoaRepository extends BaseRepository<Pessoa, Long> {
/*  aqui ficaria as querys de informações q quero tirar ou acessos ao bc*/
}
