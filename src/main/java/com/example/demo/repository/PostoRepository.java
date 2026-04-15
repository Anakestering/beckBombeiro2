package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Posto;

import java.util.List;

@Repository
public interface PostoRepository extends BaseRepository<Posto, Long> {

    List<Posto> findAllByOrderByOrdemAsc();

}