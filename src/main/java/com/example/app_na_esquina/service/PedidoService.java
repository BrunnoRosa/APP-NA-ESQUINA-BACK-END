package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.pedido.CriarPedidoDTO;
import com.example.app_na_esquina.dto.pedido.ItemPedidoDTO;
import com.example.app_na_esquina.model.*;
import com.example.app_na_esquina.model.enums.StatusPedido;
import com.example.app_na_esquina.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.app_na_esquina.dto.pedido.PedidoResponseDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public PedidoResponseDTO criarPedido(CriarPedidoDTO dto) {   // ✏️ era PedidoModel
        if (dto.getIdCliente() == null || dto.getIdMercadinho() == null) {
            throw new RegraNegocioException("Cliente e mercadinho são obrigatórios.");
        }
        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new RegraNegocioException("O pedido precisa ter pelo menos um item.");
        }

        UsuarioModel cliente = usuarioRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        // 1. Busca o mercadinho explicitamente informado no DTO
        MercadinhoModel mercadinho = mercadinhoRepository.findById(dto.getIdMercadinho())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercadinho não encontrado."));

        PedidoModel pedido = new PedidoModel();
        pedido.setCliente(cliente);
        pedido.setMercadinho(mercadinho);
        pedido.setStatus(StatusPedido.PENDENTE);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDto : dto.getItens()) {
            if (itemDto.getIdProduto() == null) {
                throw new RegraNegocioException("Há um item sem produto no pedido.");
            }
            if (itemDto.getQuantidade() == null || itemDto.getQuantidade() < 1 || itemDto.getQuantidade() > 99) {
                throw new RegraNegocioException("A quantidade de cada item deve ser de 1 a 99.");
            }

            ProdutoModel produto = produtoRepository.findById(itemDto.getIdProduto())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Um dos produtos do pedido não existe mais."));

            // 2. Trava de segurança: valida se o produto realmente pertence ao mercadinho do pedido
            if (produto.getMercadinho() != null &&
                    !produto.getMercadinho().getIdMercadinho().equals(mercadinho.getIdMercadinho())) {
                throw new RegraNegocioException("O produto '" + produto.getNome() + "' não pertence ao mercadinho informado.");
            }

            BigDecimal precoUnitario = BigDecimal.valueOf(produto.getPreco());

            ItemPedidoModel item = new ItemPedidoModel();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(precoUnitario);

            pedido.getItens().add(item);

            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
            total = total.add(subtotal);
        }

        pedido.setValorTotal(total);
        return PedidoResponseDTO.de(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorCliente(Long idCliente) {
        return pedidoRepository.findByClienteIdUsuarioOrderByDataCriacaoDesc(idCliente).stream()
                .map(PedidoResponseDTO::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorMercadinho(Long idMercadinho) {
        return pedidoRepository.findByMercadinhoIdMercadinhoOrderByDataCriacaoDesc(idMercadinho).stream()
                .map(PedidoResponseDTO::de)
                .toList();
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long idPedido, StatusPedido novoStatus, String motivo) {   // ✏️
        PedidoModel pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));

        pedido.setStatus(novoStatus);

        if (novoStatus == StatusPedido.CANCELADO && motivo != null && !motivo.trim().isEmpty()) {
            pedido.setMotivoCancelamento(motivo);
        }

        return PedidoResponseDTO.de(pedidoRepository.save(pedido));   // ✏️
    }
}