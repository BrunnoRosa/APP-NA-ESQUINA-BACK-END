package com.example.app_na_esquina.dto.produto;

import com.example.app_na_esquina.dto.categoria.CategoriaResponseDTO;

public class ProdutoResponseDTO {
    private Long idProduto;
    private String nome;
    private String descricao;
    private Double preco;
    private CategoriaResponseDTO categoria;
    private String imagemUrl;
    private Boolean emDestaque;
    private Boolean ativo;

    public ProdutoResponseDTO() {
    }

    public ProdutoResponseDTO(Long idProduto, String nome, String descricao, Double preco, CategoriaResponseDTO categoria, String imagemUrl, Boolean emDestaque, Boolean ativo) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.imagemUrl = imagemUrl;
        this.emDestaque = emDestaque;
        this.ativo = ativo;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
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

    public CategoriaResponseDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponseDTO categoria) {
        this.categoria = categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public Boolean getEmDestaque() {
        return emDestaque;
    }

    public void setEmDestaque(Boolean emDestaque) {
        this.emDestaque = emDestaque;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}