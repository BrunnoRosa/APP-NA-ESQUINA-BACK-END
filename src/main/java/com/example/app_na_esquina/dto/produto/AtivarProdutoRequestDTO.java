package com.example.app_na_esquina.dto.produto;

public class AtivarProdutoRequestDTO {
    private Long idMercadinho;
    private Long idProdutoBase;
    private Double preco;
    private Boolean ativo = true;

    public AtivarProdutoRequestDTO() {}

    public AtivarProdutoRequestDTO(Long idMercadinho, Long idProdutoBase, Double preco, Boolean ativo) {
        this.idMercadinho = idMercadinho;
        this.idProdutoBase = idProdutoBase;
        this.preco = preco;
        this.ativo = ativo;
    }

    public Long getIdMercadinho() { return idMercadinho; }
    public void setIdMercadinho(Long idMercadinho) { this.idMercadinho = idMercadinho; }

    public Long getIdProdutoBase() { return idProdutoBase; }
    public void setIdProdutoBase(Long idProdutoBase) { this.idProdutoBase = idProdutoBase; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}