package com.example.app_na_esquina.dto.pedido;


import java.util.List;

public class EditarItensPedidoDTO {
    private String motivoAjuste;
    private List<ItemAjustadoDTO> itens;

    public static class ItemAjustadoDTO {
        private Long idItemPedido;
        private Integer quantidade;

        public Long getIdItemPedido() { return idItemPedido; }
        public void setIdItemPedido(Long idItemPedido) { this.idItemPedido = idItemPedido; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }

    public String getMotivoAjuste() { return motivoAjuste; }
    public void setMotivoAjuste(String motivoAjuste) { this.motivoAjuste = motivoAjuste; }
    public List<ItemAjustadoDTO> getItens() { return itens; }
    public void setItens(List<ItemAjustadoDTO> itens) { this.itens = itens; }
}
