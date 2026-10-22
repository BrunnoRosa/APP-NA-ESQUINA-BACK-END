package com.example.app_na_esquina.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_vitrine")
public class VitrineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVitrine;

    @ManyToOne
    @JoinColumn(name = "mercadinho_id", nullable = false)
    private MercadinhoModel mercadinho;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoModel produto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Boolean disponivel = true;

    public VitrineModel() {
    }

    public VitrineModel(Long idVitrine, MercadinhoModel mercadinho, ProdutoModel produto, BigDecimal preco, Boolean disponivel) {
        this.idVitrine = idVitrine;
        this.mercadinho = mercadinho;
        this.produto = produto;
        this.preco = preco;
        this.disponivel = disponivel;
    }

    public Long getIdVitrine() {
        return idVitrine;
    }

    public void setIdVitrine(Long idVitrine) {
        this.idVitrine = idVitrine;
    }

    public MercadinhoModel getMercadinho() {
        return mercadinho;
    }

    public void setMercadinho(MercadinhoModel mercadinho) {
        this.mercadinho = mercadinho;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public void setProduto(ProdutoModel produto) {
        this.produto = produto;
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
