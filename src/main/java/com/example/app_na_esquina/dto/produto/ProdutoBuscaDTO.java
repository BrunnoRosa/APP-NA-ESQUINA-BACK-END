package com.example.app_na_esquina.dto.produto;

import com.example.app_na_esquina.model.enums.CategoriaProduto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class ProdutoBuscaDTO {
    private Long idProduto;
    private String nome;
    private String categoria;
    private BigDecimal preco;
    private Boolean emDestaque;
    private String imagemUrl;

    // Dados do Mercadinho
    private Long idMercadinho;
    private String nomeMercadinho;
    private String enderecoMercadinho;
    private Double distanciaKm;
}
