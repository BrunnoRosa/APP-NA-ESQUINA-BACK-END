package com.example.app_na_esquina.dto.pedido;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Data

public class CriarPedidoDTO {
    private Long idCliente;
    private Long idMercadinho;
    private List<ItemPedidoDTO> itens;

}
