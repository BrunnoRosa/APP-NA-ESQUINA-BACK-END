package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.model.ProdutoBaseModel;
import com.example.app_na_esquina.repository.ProdutoBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo-global")
@CrossOrigin(origins = "*")
public class ProdutoBaseController {

    @Autowired
    private ProdutoBaseRepository produtoBaseRepository;

    // Retorna todos os produtos pré-cadastrados no catálogo global
    @GetMapping
    public ResponseEntity<List<ProdutoBaseModel>> listarTodos() {
        return ResponseEntity.ok(produtoBaseRepository.findAll());
    }

    // Busca produtos no catálogo global pelo nome (Autocomplete / Barra de Pesquisa)
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoBaseModel>> buscar(@RequestParam(required = false) String nome) {
        if (nome == null || nome.isBlank()) {
            return ResponseEntity.ok(produtoBaseRepository.findAll());
        }
        return ResponseEntity.ok(produtoBaseRepository.findByNomeContainingIgnoreCase(nome.trim()));
    }

    // Busca produto global pelo Código de Barras (EAN)
    @GetMapping("/codigo-barras/{codigoBarra}")
    public ResponseEntity<ProdutoBaseModel> buscarPorCodigoBarra(@PathVariable String codigoBarra) {
        return produtoBaseRepository.findByCodigoBarra(codigoBarra)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Filtra o catálogo global por Categoria
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProdutoBaseModel>> listarPorCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(produtoBaseRepository.findByCategoriaIdCategoria(idCategoria));
    }
}