package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.PostoDTO;
import com.example.demo.entity.Posto;
import com.example.demo.repository.PostoRepository;

@Service
public class PostoService extends BaseService<Posto, PostoDTO> {

    private final PostoRepository postoRepository;

    public PostoService(PostoRepository postoRepository) {
        super(postoRepository);
        this.postoRepository = postoRepository;
    }

    
    public List<PostoDTO> listarOrdenado() {
    return postoRepository.findAllByAtivoTrueOrderByOrdemAsc()
            .stream()
            .map(this::toDto)
            .toList();
}
}