package com.example.app_na_esquina.dto.vitrine;


import java.math.BigDecimal;


public class VitrineResponseDTO {
    private Long idVitrine;
    private Long idMercadinho;
    private Long idProduto;
    private String nomeProduto; // Facilita a exibição no App
    private String imagemProduto; // Facilita a exibição no App
    private BigDecimal preco;
    private Boolean disponivel;
    // (Nota: Neste DTO de resposta, é muito útil enviar o nome e a imagem do produto junto,
    // para o aplicativo front-end não precisar fazer duas requisições separadas).

    public VitrineResponseDTO() {
    }

    public VitrineResponseDTO(Long idVitrine, Long idMercadinho, Long idProduto, String nomeProduto, String imagemProduto, BigDecimal preco, Boolean disponivel) {
        this.idVitrine = idVitrine;
        this.idMercadinho = idMercadinho;
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.imagemProduto = imagemProduto;
        this.preco = preco;
        this.disponivel = disponivel;
    }

    public Long getIdVitrine() {
        return idVitrine;
    }

    public void setIdVitrine(Long idVitrine) {
        this.idVitrine = idVitrine;
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

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getImagemProduto() {
        return imagemProduto;
    }

    public void setImagemProduto(String imagemProduto) {
        this.imagemProduto = imagemProduto;
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

