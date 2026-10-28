package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.produto.AtivarProdutoRequestDTO;
import com.example.app_na_esquina.dto.produto.ProdutoBuscaDTO;
import com.example.app_na_esquina.model.CategoriaModel;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.ProdutoBaseModel;
import com.example.app_na_esquina.model.ProdutoModel;
import com.example.app_na_esquina.repository.CategoriaRepository;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import com.example.app_na_esquina.repository.ProdutoBaseRepository;
import com.example.app_na_esquina.repository.ProdutoRepository;
import com.example.app_na_esquina.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoBaseRepository produtoBaseRepository;

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    @Autowired
    private MercadinhoService mercadinhoService;

    public ProdutoModel salvar(ProdutoModel produto) {
        return produtoRepository.save(produto);
    }

    // Método para Ativar/Vincular Produto do Catálogo Global ao Mercadinho
    @Transactional
    public ProdutoModel ativarOuAtualizarProdutoDoCatalogo(AtivarProdutoRequestDTO dto) {
        // Busca flexível: primeiro pelo id_mercadinho, se não achar tenta pelo comerciante_id (idUsuario)
        MercadinhoModel mercadinho = mercadinhoRepository.findById(dto.getIdMercadinho())
                .orElseGet(() -> mercadinhoRepository.findByUsuarioIdUsuarioNative(dto.getIdMercadinho())
                        .orElseThrow(() -> new RuntimeException("Mercadinho não encontrado para o ID enviado: " + dto.getIdMercadinho())));

        ProdutoBaseModel produtoBase = produtoBaseRepository.findById(dto.getIdProdutoBase())
                .orElseThrow(() -> new RuntimeException("Produto base não encontrado com ID: " + dto.getIdProdutoBase()));

        // Utiliza a chave primária real do mercadinho recuperado para a consulta composta
        Optional<ProdutoModel> produtoExistente = produtoRepository
                .findByMercadinhoIdMercadinhoAndProdutoBaseIdProdutoBase(mercadinho.getIdMercadinho(), dto.getIdProdutoBase());

        ProdutoModel produto;
        if (produtoExistente.isPresent()) {
            produto = produtoExistente.get();
            produto.setPreco(dto.getPreco());
            produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        } else {
            produto = new ProdutoModel();
            produto.setMercadinho(mercadinho);
            produto.setProdutoBase(produtoBase);
            produto.setPreco(dto.getPreco());
            produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
            produto.setEmDestaque(false);
        }

        return produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoModel atualizar(Long id, ProdutoModel dadosNovos, Long idCategoria) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        if (dadosNovos.getPreco() != null) {
            produto.setPreco(dadosNovos.getPreco());
        }
        if (dadosNovos.getNomePersonalizado() != null) {
            produto.setNomePersonalizado(dadosNovos.getNomePersonalizado());
        }
        if (dadosNovos.getImagemUrlPersonalizada() != null) {
            produto.setImagemUrlPersonalizada(dadosNovos.getImagemUrlPersonalizada());
        }

        return produtoRepository.save(produto);
    }

    public List<ProdutoModel> listarTodos() {
        return produtoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                .collect(Collectors.toList());
    }

    public Optional<ProdutoModel> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    // Traz TODOS os produtos (ativos e inativos) para a tela de gestão do comerciante
    public List<ProdutoModel> buscarPorMercadinho(Long idMercadinho) {
        return produtoRepository.findByMercadinhoIdMercadinho(idMercadinho);
    }

    // Traz APENAS produtos ativos do mercadinho (para o cliente)
    public List<ProdutoModel> buscarAtivosPorMercadinho(Long idMercadinho) {
        return produtoRepository.findByMercadinhoIdMercadinhoAndAtivoTrue(idMercadinho);
    }

    // Método para Alternar Status (Ativar / Inativar)
    @Transactional
    public ProdutoModel alternarStatus(Long id, Boolean ativo) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        produto.setAtivo(ativo);

        // Se for inativado, remove automaticamente o destaque para sumir da Home do cliente
        if (Boolean.FALSE.equals(ativo)) {
            produto.setEmDestaque(false);
        }

        return produtoRepository.save(produto);
    }

    public List<ProdutoBuscaDTO> buscarProdutosGlobal(String nome, String categoria, Double userLat, Double userLng, String ordenarPor) {
        List<ProdutoModel> produtos = produtoRepository.findByProdutoBaseNomeContainingIgnoreCaseOrNomePersonalizadoContainingIgnoreCase(nome, nome);

        List<ProdutoBuscaDTO> dtos = produtos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getAtivo())) // APENAS ATIVOS NA HOME/BUSCA DO CLIENTE
                .filter(p -> {
                    if (categoria == null || categoria.trim().isEmpty() || "TODOS".equalsIgnoreCase(categoria)) {
                        return true;
                    }
                    return p.getCategoria() != null && p.getCategoria().getNome().equalsIgnoreCase(categoria);
                }).map(p -> {
                    ProdutoBuscaDTO dto = new ProdutoBuscaDTO();
                    dto.setIdProduto(p.getIdProduto());
                    dto.setNome(p.getNome());
                    dto.setCategoria(p.getCategoria() != null ? p.getCategoria().getNome() : null);
                    dto.setPreco(p.getPreco() != null ? BigDecimal.valueOf(p.getPreco()) : null);
                    dto.setEmDestaque(Boolean.TRUE.equals(p.getEmDestaque()));
                    dto.setImagemUrl(p.getImagemUrl());

                    if (p.getMercadinho() != null) {
                        dto.setIdMercadinho(p.getMercadinho().getIdMercadinho());
                        dto.setNomeMercadinho(p.getMercadinho().getNomeFantasia());
                        dto.setEnderecoMercadinho(p.getMercadinho().getEndereco());

                        if (userLat != null && userLng != null && p.getMercadinho().getLatitude() != null && p.getMercadinho().getLongitude() != null) {
                            double dist = GeoUtils.calcularDistanciaKm(userLat, userLng, p.getMercadinho().getLatitude(), p.getMercadinho().getLongitude());
                            dto.setDistanciaKm(dist);
                        } else {
                            dto.setDistanciaKm(1.2);
                        }
                    }
                    return dto;
                }).collect(Collectors.toList());

        if ("distancia".equalsIgnoreCase(ordenarPor)) {
            dtos.sort(Comparator.comparing(ProdutoBuscaDTO::getDistanciaKm, Comparator.nullsLast(Double::compareTo)));
        } else {
            dtos.sort(Comparator.comparing(ProdutoBuscaDTO::getPreco, Comparator.nullsLast(BigDecimal::compareTo)));
        }

        return dtos;
    }

    public List<ProdutoModel> buscarPorNome(String nome) {
        return produtoRepository.findByProdutoBaseNomeContainingIgnoreCaseOrNomePersonalizadoContainingIgnoreCase(nome, nome).stream()
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                .collect(Collectors.toList());
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }
}