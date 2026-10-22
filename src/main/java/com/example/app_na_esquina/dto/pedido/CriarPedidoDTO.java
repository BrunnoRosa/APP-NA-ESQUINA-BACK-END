package com.example.app_na_esquina.dto.pedido;


import java.util.List;

public class CriarPedidoDTO {
    private Long idCliente;
    private Long idMercadinho;
    private List<ItemPedidoDTO> itens;

    public CriarPedidoDTO() {
    }

    public CriarPedidoDTO(Long idCliente, Long idMercadinho, List<ItemPedidoDTO> itens) {
        this.idCliente = idCliente;
        this.idMercadinho = idMercadinho;
        this.itens = itens;
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
}
