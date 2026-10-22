package com.example.app_na_esquina.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tab_itens_pedidos")
public class ItemPedidoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemPedido;
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonIgnore // Evita recursão infinita na serialização do JSON
    private PedidoModel pedido;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private ProdutoModel produto;

    private Integer quantidade;
    private BigDecimal precoUnitario;

    public ItemPedidoModel() {
    }

    public ItemPedidoModel(Long idItemPedido, PedidoModel pedido, ProdutoModel produto, Integer quantidade, BigDecimal precoUnitario) {
        this.idItemPedido = idItemPedido;
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Long getIdItemPedido() {
        return idItemPedido;
    }

    public void setIdItemPedido(Long idItemPedido) {
        this.idItemPedido = idItemPedido;
    }

    public PedidoModel getPedido() {
        return pedido;
    }

    public void setPedido(PedidoModel pedido) {
        this.pedido = pedido;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public void setProduto(ProdutoModel produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
