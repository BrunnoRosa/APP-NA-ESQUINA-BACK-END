package com.example.app_na_esquina.dto.produto;

import java.math.BigDecimal;

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

    public ProdutoBuscaDTO() {
    }

    public ProdutoBuscaDTO(Long idProduto, String nome, String categoria, BigDecimal preco, Boolean emDestaque, String imagemUrl, Long idMercadinho, String nomeMercadinho, String enderecoMercadinho, Double distanciaKm) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.emDestaque = emDestaque;
        this.imagemUrl = imagemUrl;
        this.idMercadinho = idMercadinho;
        this.nomeMercadinho = nomeMercadinho;
        this.enderecoMercadinho = enderecoMercadinho;
        this.distanciaKm = distanciaKm;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Boolean getEmDestaque() {
        return emDestaque;
    }

    public void setEmDestaque(Boolean emDestaque) {
        this.emDestaque = emDestaque;
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

    public String getNomeMercadinho() {
        return nomeMercadinho;
    }

    public void setNomeMercadinho(String nomeMercadinho) {
        this.nomeMercadinho = nomeMercadinho;
    }

    public String getEnderecoMercadinho() {
        return enderecoMercadinho;
    }

    public void setEnderecoMercadinho(String enderecoMercadinho) {
        this.enderecoMercadinho = enderecoMercadinho;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
}
