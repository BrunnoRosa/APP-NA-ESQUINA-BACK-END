package com.example.app_na_esquina.dto.pedido;



public class ItemPedidoDTO {
    private Long idProduto;
    private Integer quantidade;

    public ItemPedidoDTO() {
    }

    public ItemPedidoDTO(Long idProduto, Integer quantidade) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
