package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PostoDTO;
import com.example.demo.service.PostoService;



@RestController
@RequestMapping("/postos")
public class PostoController extends BaseController<PostoDTO> {

    public PostoController(PostoService service) {
        super(service);
    }

    @GetMapping("/ordenado")
    public List<PostoDTO> listarOrdenado() {
        return ((PostoService) service).listarOrdenado();
    }
}
