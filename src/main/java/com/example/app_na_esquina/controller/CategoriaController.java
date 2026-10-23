package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.model.CategoriaModel;
import com.example.app_na_esquina.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public ResponseEntity<List<CategoriaModel>> listarTodas() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<CategoriaModel> criar(@RequestBody CategoriaModel categoria) {
        CategoriaModel salva = categoriaRepository.save(categoria);
        return ResponseEntity.status(201).body(salva);
    }
}