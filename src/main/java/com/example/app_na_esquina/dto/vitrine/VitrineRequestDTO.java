package com.example.app_na_esquina.dto.vitrine;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class VitrineRequestDTO {
    private Long idMercadinho;
    private Long idProduto;
    private BigDecimal preco;
    private Boolean disponivel;
}