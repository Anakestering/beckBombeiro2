package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.entity.Posto;
import com.example.demo.repository.PostoRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initPostos(PostoRepository repository) {
        return args -> {

            if (repository.count() == 0) {
                for (int i = 1; i <= 21; i++) {
                    Posto p = new Posto();
                    p.setNome("Posto " + i);
                    p.setOrdem(i);
                    repository.save(p);
                }
            }

        };
    }
}