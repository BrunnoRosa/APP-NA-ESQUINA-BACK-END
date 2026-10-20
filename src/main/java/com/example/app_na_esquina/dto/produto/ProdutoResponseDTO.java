package com.example.app_na_esquina.dto.produto;

import com.example.app_na_esquina.model.enums.CategoriaProduto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoResponseDTO {
    private Long idProduto;
    private String nome;
    private String descricao;
    private Double preco;
    private CategoriaProduto categoria;
    private String imagemUrl;
    private Boolean emDestaque;
    private Boolean ativo;
}