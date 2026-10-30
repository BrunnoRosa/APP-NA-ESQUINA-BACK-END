package com.example.app_na_esquina.dto.pedido;


import java.util.List;

public class CriarPedidoDTO {
    private Long idCliente;
    private Long idMercadinho;
    private List<ItemPedidoDTO> itens;
    private Double valorTotal;

    public CriarPedidoDTO() {
    }

    public CriarPedidoDTO(Long idCliente, Long idMercadinho, List<ItemPedidoDTO> itens, Double valorTotal) {
        this.idCliente = idCliente;
        this.idMercadinho = idMercadinho;
        this.itens = itens;
        this.valorTotal = valorTotal;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdMercadinho() {
        return idMercadinho;
    }

    public void setIdMercadinho(Long idMercadinho) {
        this.idMercadinho = idMercadinho;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
