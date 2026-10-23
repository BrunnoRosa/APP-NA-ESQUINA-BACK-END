package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.categoria.CategoriaResponseDTO;
import com.example.app_na_esquina.dto.produto.AtivarProdutoRequestDTO;
import com.example.app_na_esquina.dto.produto.ProdutoBuscaDTO;
import com.example.app_na_esquina.dto.produto.ProdutoRequestDTO;
import com.example.app_na_esquina.dto.produto.ProdutoResponseDTO;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.ProdutoModel;
import com.example.app_na_esquina.service.MercadinhoService;
import com.example.app_na_esquina.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private MercadinhoService mercadinhoService;

    // Novo Endpoint: Ativar/Vincular um produto do Catálogo Global ao Mercadinho
    @PostMapping("/ativar-do-catalogo")
    public ResponseEntity<ProdutoResponseDTO> ativarDoCatalogo(@RequestBody AtivarProdutoRequestDTO dto) {
        ProdutoModel produtoAtivado = produtoService.ativarProdutoDoCatalogo(dto);
        return ResponseEntity.status(201).body(converterParaDTO(produtoAtivado));
    }

    // Cadastro manual de produto personalizado
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@RequestBody ProdutoRequestDTO dto) {
        ProdutoModel model = new ProdutoModel();
        model.setPreco(dto.getPreco());
        model.setNomePersonalizado(dto.getNome());
        model.setImagemUrlPersonalizada(dto.getImagemUrl());

        if (dto.getIdMercadinho() != null) {
            MercadinhoModel mercadinho = mercadinhoService.buscarPorId(dto.getIdMercadinho())
                    .orElseThrow(() -> new RuntimeException("Mercadinho não encontrado com ID: " + dto.getIdMercadinho()));
            model.setMercadinho(mercadinho);
        }

        ProdutoModel salvo = produtoService.salvar(model);
        return ResponseEntity.status(201).body(converterParaDTO(salvo));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    // Endpoint de Busca Global de Produtos (Comparador de Preços e Distâncias)
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoBuscaDTO>> buscarProdutosGlobal(
            @RequestParam("nome") String nome,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "lat", required = false) Double userLat,
            @RequestParam(value = "lng", required = false) Double userLng,
            @RequestParam(value = "ordenarPor", required = false, defaultValue = "preco") String ordenarPor) {

        List<ProdutoBuscaDTO> dtos = produtoService.buscarProdutosGlobal(nome, categoria, userLat, userLng, ordenarPor);
        return ResponseEntity.ok(dtos);
    }

    // Listagem para o COMERCIANTE (Retorna todos os produtos do estoque do mercadinho)
    @GetMapping("/mercadinho/{idMercadinho}")
    public ResponseEntity<List<ProdutoResponseDTO>> listarPorMercadinho(@PathVariable Long idMercadinho) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarPorMercadinho(idMercadinho).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    // Listagem para o CLIENTE (Retorna apenas produtos ATIVOS do mercadinho)
    @GetMapping("/mercadinho/{idMercadinho}/cliente")
    public ResponseEntity<List<ProdutoResponseDTO>> listarParaCliente(@PathVariable Long idMercadinho) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarAtivosPorMercadinho(idMercadinho).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @RequestBody ProdutoRequestDTO dto) {
        ProdutoModel novosDados = new ProdutoModel();
        novosDados.setPreco(dto.getPreco());
        novosDados.setNomePersonalizado(dto.getNome());
        novosDados.setImagemUrlPersonalizada(dto.getImagemUrl());

        ProdutoModel atualizado = produtoService.atualizar(id, novosDados, dto.getIdCategoria());
        return ResponseEntity.ok(converterParaDTO(atualizado));
    }

    @PutMapping("/{id}/destaque")
    public ResponseEntity<ProdutoResponseDTO> alternarDestaque(
            @PathVariable("id") Long id,
            @RequestParam("emDestaque") Boolean emDestaque) {

        ProdutoModel model = produtoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        model.setEmDestaque(emDestaque != null ? emDestaque : false);

        ProdutoModel atualizado = produtoService.salvar(model);
        return ResponseEntity.ok(converterParaDTO(atualizado));
    }

    // Endpoint para Alternar Status (Ativar / Inativar)
    @PutMapping("/{id}/status")
    public ResponseEntity<ProdutoResponseDTO> alternarStatus(
            @PathVariable Long id,
            @RequestParam("ativo") Boolean ativo) {
        ProdutoModel atualizado = produtoService.alternarStatus(id, ativo);
        return ResponseEntity.ok(converterParaDTO(atualizado));
    }

    // Mantido por compatibilidade
    @PutMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        produtoService.alternarStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private ProdutoResponseDTO converterParaDTO(ProdutoModel model) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setIdProduto(model.getIdProduto());
        dto.setNome(model.getNome()); // Utiliza o método utilitário que escolhe nomePersonalizado ou produtoBase.getNome()
        dto.setPreco(model.getPreco());
        dto.setImagemUrl(model.getImagemUrl()); // Utiliza a imagem do catálogo base ou personalizada
        dto.setEmDestaque(Boolean.TRUE.equals(model.getEmDestaque()));
        dto.setAtivo(model.getAtivo() != null ? model.getAtivo() : true);

        if (model.getProdutoBase() != null) {
            dto.setDescricao(model.getProdutoBase().getDescricao());
        }

        if (model.getCategoria() != null) {
            CategoriaResponseDTO catDto = new CategoriaResponseDTO();
            catDto.setIdCategoria(model.getCategoria().getIdCategoria());
            catDto.setNome(model.getCategoria().getNome());
            catDto.setIconeUrl(model.getCategoria().getIconeUrl());
            dto.setCategoria(catDto);
        }

        return dto;
    }
}