package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.pedido.CriarPedidoDTO;
import com.example.app_na_esquina.dto.pedido.ItemPedidoDTO;
import com.example.app_na_esquina.model.*;
import com.example.app_na_esquina.model.enums.StatusPedido;
import com.example.app_na_esquina.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PedidoModel criarPedido(CriarPedidoDTO dto) {
        UsuarioModel cliente = usuarioRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        MercadinhoModel mercadinho = mercadinhoRepository.findById(dto.getIdMercadinho())
                .orElseThrow(() -> new RuntimeException("Mercadinho não encontrado"));

        PedidoModel pedido = new PedidoModel();
        pedido.setCliente(cliente);
        pedido.setMercadinho(mercadinho);
        pedido.setStatus(StatusPedido.PENDENTE);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDto : dto.getItens()) {
            ProdutoModel produto = produtoRepository.findById(itemDto.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDto.getIdProduto()));

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
        return pedidoRepository.save(pedido);
    }

    public List<PedidoModel> buscarPorCliente(Long idCliente) {
        return pedidoRepository.findByClienteIdUsuarioOrderByDataCriacaoDesc(idCliente);
    }

    public List<PedidoModel> buscarPorMercadinho(Long idMercadinho) {
        return pedidoRepository.findByMercadinhoIdMercadinhoOrderByDataCriacaoDesc(idMercadinho);
    }

    @Transactional
    public PedidoModel atualizarStatus(Long idPedido, StatusPedido novoStatus, String motivo) {
        PedidoModel pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + idPedido));

        pedido.setStatus(novoStatus);

        // Se o status for CANCELADO e houver motivo informado, atribui à entidade
        if (novoStatus == StatusPedido.CANCELADO && motivo != null && !motivo.trim().isEmpty()) {
            pedido.setMotivoCancelamento(motivo);
        }

        return pedidoRepository.save(pedido);
    }
}