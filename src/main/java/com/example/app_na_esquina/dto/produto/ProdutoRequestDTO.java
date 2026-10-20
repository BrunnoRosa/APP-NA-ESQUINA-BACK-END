package com.example.app_na_esquina.dto.produto;

import com.example.app_na_esquina.model.enums.CategoriaProduto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ProdutoRequestDTO {
    private String nome;
    private String descricao;
    private Double preco;
    private CategoriaProduto categoria;
    private String imagemUrl;
    private Long idMercadinho;
    private Boolean emEstoque;
}