package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.produto.AtivarProdutoRequestDTO;
import com.example.app_na_esquina.dto.produto.ProdutoBuscaDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.CategoriaModel;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.ProdutoBaseModel;
import com.example.app_na_esquina.model.ProdutoModel;
import com.example.app_na_esquina.repository.CategoriaRepository;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import com.example.app_na_esquina.repository.ProdutoBaseRepository;
import com.example.app_na_esquina.repository.ProdutoRepository;
import com.example.app_na_esquina.util.GeoUtils;
import com.example.app_na_esquina.util.Validacoes;
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

    public static final int LIMITE_PROMOCOES_POR_MERCADINHO = 10;

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

    @Transactional
    public ProdutoModel ativarOuAtualizarProdutoDoCatalogo(AtivarProdutoRequestDTO dto, String emailComercianteLogado) {
        Validacoes.preco(dto.getPreco());

        MercadinhoModel mercadinho = mercadinhoRepository.buscarPorEmailDoUsuario(emailComercianteLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercadinho não encontrado para o comerciante autenticado."));

        ProdutoBaseModel produtoBase = produtoBaseRepository.findById(dto.getIdProdutoBase())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto base não encontrado com ID: " + dto.getIdProdutoBase()));

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

    public CategoriaModel buscarCategoriaPorId(Long idCategoria) {
        if (idCategoria == null) return null;
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada: " + idCategoria));
    }

    @Transactional
    public ProdutoModel atualizar(Long id, ProdutoModel dadosNovos, Long idCategoria) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));

        if (dadosNovos.getPreco() != null) {
            produto.setPreco(dadosNovos.getPreco());
            // Se o comerciante baixar o preço normal para menor/igual ao promocional, encerra a promoção
            if (produto.getPrecoPromocional() != null && produto.getPrecoPromocional() >= dadosNovos.getPreco()) {
                produto.setEmPromocao(false);
                produto.setPrecoPromocional(null);
            }
        }
        if (dadosNovos.getNomePersonalizado() != null) {
            produto.setNomePersonalizado(dadosNovos.getNomePersonalizado());
        }
        if (dadosNovos.getImagemUrlPersonalizada() != null) {
            produto.setImagemUrlPersonalizada(dadosNovos.getImagemUrlPersonalizada());
        }
        if (idCategoria != null) {
            produto.setCategoriaPersonalizada(buscarCategoriaPorId(idCategoria));
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

    public List<ProdutoModel> buscarPorMercadinho(Long idMercadinho) {
        return produtoRepository.findByMercadinhoIdMercadinho(idMercadinho);
    }

    public List<ProdutoModel> buscarAtivosPorMercadinho(Long idMercadinho) {
        return produtoRepository.findByMercadinhoIdMercadinhoAndAtivoTrue(idMercadinho);
    }

    @Transactional
    public ProdutoModel alternarPromocao(Long id, Boolean emPromocao, Double precoPromocional) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + id));

        if (Boolean.TRUE.equals(emPromocao)) {
            if (Boolean.FALSE.equals(produto.getAtivo())) {
                throw new RegraNegocioException("Ative o produto antes de colocá-lo em promoção.");
            }

            Validacoes.preco(precoPromocional);

            if (produto.getPreco() != null && precoPromocional >= produto.getPreco()) {
                throw new RegraNegocioException(
                        String.format("O preço promocional deve ser menor que o preço atual (R$ %.2f).", produto.getPreco())
                );
            }

            if (!Boolean.TRUE.equals(produto.getEmPromocao()) && produto.getMercadinho() != null) {
                long qtdAtual = produtoRepository.countByMercadinhoIdMercadinhoAndEmPromocaoTrue(
                        produto.getMercadinho().getIdMercadinho()
                );
                if (qtdAtual >= LIMITE_PROMOCOES_POR_MERCADINHO) {
                    throw new RegraNegocioException(
                            "Limite atingido: cada mercadinho pode ter no máximo " + LIMITE_PROMOCOES_POR_MERCADINHO + " itens em promoção."
                    );
                }
            }

            produto.setEmPromocao(true);
            produto.setPrecoPromocional(precoPromocional);
        } else {
            produto.setEmPromocao(false);
            produto.setPrecoPromocional(null);
        }

        return produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoModel alternarStatus(Long id, Boolean ativo) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + id));

        produto.setAtivo(ativo);

        if (Boolean.FALSE.equals(ativo)) {
            produto.setEmDestaque(false);
            produto.setEmPromocao(false);
            produto.setPrecoPromocional(null);
        }

        return produtoRepository.save(produto);
    }

    public List<ProdutoBuscaDTO> buscarProdutosGlobal(String nome, String categoria, Double userLat, Double userLng, String ordenarPor) {
        List<ProdutoModel> produtos = produtoRepository.findByProdutoBaseNomeContainingIgnoreCaseOrNomePersonalizadoContainingIgnoreCase(nome, nome);

        List<ProdutoBuscaDTO> dtos = produtos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
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

                    // Dados da promoção (De / Por)
                    boolean promoValida = Boolean.TRUE.equals(p.getEmPromocao())
                            && p.getPrecoPromocional() != null
                            && p.getPreco() != null
                            && p.getPrecoPromocional() < p.getPreco();
                    dto.setEmPromocao(promoValida);
                    dto.setPrecoPromocional(promoValida ? BigDecimal.valueOf(p.getPrecoPromocional()) : null);

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
            dtos.sort(Comparator.comparing(
                    d -> Boolean.TRUE.equals(d.getEmPromocao()) && d.getPrecoPromocional() != null
                            ? d.getPrecoPromocional()
                            : d.getPreco(),
                    Comparator.nullsLast(BigDecimal::compareTo)
            ));
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