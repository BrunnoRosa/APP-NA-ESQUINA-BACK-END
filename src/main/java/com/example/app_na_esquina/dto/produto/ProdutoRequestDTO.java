package com.example.app_na_esquina.dto.produto;

public class ProdutoRequestDTO {
    private String nome;
    private String descricao;
    private Double preco;
    private Long idCategoria;
    private String imagemUrl;
    private Long idMercadinho;
    private Boolean emEstoque;

    public ProdutoRequestDTO() {
    }

    public ProdutoRequestDTO(String nome, String descricao, Double preco, Long idCategoria, String imagemUrl, Long idMercadinho, Boolean emEstoque) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.idCategoria = idCategoria;
        this.imagemUrl = imagemUrl;
        this.idMercadinho = idMercadinho;
        this.emEstoque = emEstoque;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public Long getIdMercadinho() {
        return idMercadinho;
    }

    public void setIdMercadinho(Long idMercadinho) {
        this.idMercadinho = idMercadinho;
    }

    public Boolean getEmEstoque() {
        return emEstoque;
    }

    public void setEmEstoque(Boolean emEstoque) {
        this.emEstoque = emEstoque;
    }
}