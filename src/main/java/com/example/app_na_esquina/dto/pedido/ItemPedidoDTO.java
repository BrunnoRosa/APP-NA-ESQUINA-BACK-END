package com.example.app_na_esquina.dto.pedido;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ItemPedidoDTO {
    private Long idProduto;
    private Integer quantidade;

}
