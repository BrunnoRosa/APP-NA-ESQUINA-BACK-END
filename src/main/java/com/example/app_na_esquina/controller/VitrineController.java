package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.vitrine.VitrineRequestDTO;
import com.example.app_na_esquina.dto.vitrine.VitrineResponseDTO;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.ProdutoModel;
import com.example.app_na_esquina.model.VitrineModel;
import com.example.app_na_esquina.service.MercadinhoService;
import com.example.app_na_esquina.service.ProdutoService;
import com.example.app_na_esquina.service.VitrineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vitrines")
public class VitrineController {

    @Autowired
    private VitrineService vitrineService;

    @Autowired
    private MercadinhoService mercadinhoService;

    // Repare que aqui precisamos injetar o ProdutoRepository ou fazer uma busca simples no ProdutoService
    // Para simplificar, assumimos que o produto existe. Na prática, você validaria se ele existe no banco.

    @PostMapping
    public ResponseEntity<VitrineResponseDTO> adicionarProduto(@RequestBody VitrineRequestDTO dto) {
        VitrineModel model = new VitrineModel();

        MercadinhoModel mercadinho = new MercadinhoModel();
        mercadinho.setIdMercadinho(dto.getIdMercadinho());

        ProdutoModel produto = new ProdutoModel();
        produto.setIdProduto(dto.getIdProduto());

        model.setMercadinho(mercadinho);
        model.setProduto(produto);
        model.setPreco(dto.getPreco());
        model.setDisponivel(dto.getDisponivel() != null ? dto.getDisponivel() : true);

        VitrineModel salvo = vitrineService.adicionarProduto(model);

        // Em um cenário real, você buscaria o produto completo no banco para preencher o nome no DTO.
        // Aqui retornamos o básico após salvar.
        return ResponseEntity.status(201).body(converterParaDTO(salvo));
    }

    // Rota para o DONO DO MERCADINHO (vê tudo, inclusive esgotados)
    @GetMapping("/mercadinho/{idMercadinho}/todos")
    public ResponseEntity<List<VitrineResponseDTO>> listarTodosDoMercadinho(@PathVariable Long idMercadinho) {
        List<VitrineResponseDTO> itens = vitrineService.listarVitrineDoMercadinho(idMercadinho).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itens);
    }

    // Rota para o CLIENTE/MORADOR (vê apenas o que tem em estoque)
    @GetMapping("/mercadinho/{idMercadinho}/disponiveis")
    public ResponseEntity<List<VitrineResponseDTO>> listarDisponiveisDoMercadinho(@PathVariable Long idMercadinho) {
        List<VitrineResponseDTO> itens = vitrineService.listarDisponiveisDoMercadinho(idMercadinho).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itens);
    }

    // Rota para mudar o status de "disponível" (o clique rápido do comerciante)
    @PatchMapping("/{idVitrine}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long idVitrine, @RequestParam boolean disponivel) {
        vitrineService.atualizarDisponibilidade(idVitrine, disponivel);
        return ResponseEntity.noContent().build();
    }

    private VitrineResponseDTO converterParaDTO(VitrineModel model) {
        VitrineResponseDTO dto = new VitrineResponseDTO();
        dto.setIdVitrine(model.getIdVitrine());
        dto.setIdMercadinho(model.getMercadinho().getIdMercadinho());
        dto.setIdProduto(model.getProduto().getIdProduto());

        // Pega os dados do produto aninhado para enviar ao Front-end
        if (model.getProduto() != null && model.getProduto().getNome() != null) {
            dto.setNomeProduto(model.getProduto().getNome());
            dto.setImagemProduto(model.getProduto().getImagemUrl());
        }

        dto.setPreco(model.getPreco());
        dto.setDisponivel(model.getDisponivel());
        return dto;
    }
}
