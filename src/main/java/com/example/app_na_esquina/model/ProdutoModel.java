package com.example.app_na_esquina.model;

import com.example.app_na_esquina.model.enums.CategoriaProduto;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_produtos")
public class ProdutoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Double preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaProduto categoria;

    private String imagemUrl;

    @ManyToOne
    @JoinColumn(name = "id_mercadinho")
    private MercadinhoModel mercadinho;

    @Column(name = "em_destaque")
    private Boolean emDestaque = false;

    @Column(columnDefinition = "boolean default true")
    private Boolean ativo = true;

    public ProdutoModel() {
    }

    public ProdutoModel(Long idProduto, String nome, String descricao, Double preco, CategoriaProduto categoria, String imagemUrl, MercadinhoModel mercadinho, Boolean emDestaque, Boolean ativo) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.imagemUrl = imagemUrl;
        this.mercadinho = mercadinho;
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

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public MercadinhoModel getMercadinho() {
        return mercadinho;
    }

    public void setMercadinho(MercadinhoModel mercadinho) {
        this.mercadinho = mercadinho;
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