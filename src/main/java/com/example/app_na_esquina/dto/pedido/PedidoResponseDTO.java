package com.example.app_na_esquina.dto.pedido;

import com.example.app_na_esquina.model.ItemPedidoModel;
import com.example.app_na_esquina.model.PedidoModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long idPedido,
        String status,
        BigDecimal valorTotal,
        LocalDateTime dataCriacao,
        String motivoCancelamento,
        ClienteResumo cliente,
        MercadinhoResumo mercadinho,
        List<ItemResumo> itens
) {

    public record ClienteResumo(Long idUsuario, String nome, String email) {}

    public record MercadinhoResumo(Long idMercadinho, String nomeFantasia, String endereco) {}

    public record ProdutoResumo(Long idProduto, String nome, String imagemUrl) {}

    public record ItemResumo(Long idItemPedido, Integer quantidade, BigDecimal precoUnitario, ProdutoResumo produto) {}

    // Converte a entidade do banco no objeto enxuto que vai para o app
    public static PedidoResponseDTO de(PedidoModel p) {
        ClienteResumo cliente = p.getCliente() == null ? null
                : new ClienteResumo(
                p.getCliente().getIdUsuario(),
                p.getCliente().getNome(),
                p.getCliente().getEmail());

        MercadinhoResumo mercadinho = p.getMercadinho() == null ? null
                : new MercadinhoResumo(
                p.getMercadinho().getIdMercadinho(),
                p.getMercadinho().getNomeFantasia(),
                p.getMercadinho().getEndereco());

        List<ItemResumo> itens = p.getItens().stream()
                .map(PedidoResponseDTO::converterItem)
                .toList();

        return new PedidoResponseDTO(
                p.getIdPedido(),
                p.getStatus() == null ? null : p.getStatus().name(),
                p.getValorTotal(),
                p.getDataCriacao(),
                p.getMotivoCancelamento(),
                cliente,
                mercadinho,
                itens);
    }

    private static ItemResumo converterItem(ItemPedidoModel i) {
        ProdutoResumo produto = i.getProduto() == null ? null
                : new ProdutoResumo(
                i.getProduto().getIdProduto(),
                i.getProduto().getNome(),
                i.getProduto().getImagemUrl());

        return new ItemResumo(i.getIdItemPedido(), i.getQuantidade(), i.getPrecoUnitario(), produto);
    }
}