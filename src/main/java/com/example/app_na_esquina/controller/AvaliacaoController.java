package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.avaliacao.AvaliacaoResponseDTO;
import com.example.app_na_esquina.dto.avaliacao.CriarAvaliacaoDTO;
import com.example.app_na_esquina.dto.avaliacao.ResumoAvaliacoesMercadinhoDTO;
import com.example.app_na_esquina.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> criar(
            @RequestBody CriarAvaliacaoDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(avaliacaoService.avaliarPedido(dto, authentication.getName()));
    }

    @GetMapping("/mercadinho/{idMercadinho}")
    public ResponseEntity<ResumoAvaliacoesMercadinhoDTO> listarPorMercadinho(
            @PathVariable Long idMercadinho) {
        return ResponseEntity.ok(avaliacaoService.obterResumoPorMercadinho(idMercadinho));
    }

    @GetMapping("/meus-pedidos-avaliados")
    public ResponseEntity<List<Long>> meusPedidosAvaliados(Authentication authentication) {
        return ResponseEntity.ok(avaliacaoService.listarIdsPedidosAvaliados(authentication.getName()));
    }
}