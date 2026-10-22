package com.example.app_na_esquina.dto.vitrine;


import java.math.BigDecimal;


public class VitrineRequestDTO {
    private Long idMercadinho;
    private Long idProduto;
    private BigDecimal preco;
    private Boolean disponivel;

    public VitrineRequestDTO() {
    }

    public VitrineRequestDTO(Long idMercadinho, Long idProduto, BigDecimal preco, Boolean disponivel) {
        this.idMercadinho = idMercadinho;
        this.idProduto = idProduto;
        this.preco = preco;
        this.disponivel = disponivel;
    }

    public Long getIdMercadinho() {
        return idMercadinho;
    }

    public void setIdMercadinho(Long idMercadinho) {
        this.idMercadinho = idMercadinho;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }
}