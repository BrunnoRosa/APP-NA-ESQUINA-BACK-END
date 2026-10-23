package com.example.app_na_esquina.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_produtos_base")
public class ProdutoBaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProdutoBase;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    private String codigoBarra; // Código EAN (ex: 7894900011517)

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String imagemUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaModel categoria;

    public ProdutoBaseModel() {}

    public ProdutoBaseModel(Long idProdutoBase, String nome, String codigoBarra, String descricao, String imagemUrl, CategoriaModel categoria) {
        this.idProdutoBase = idProdutoBase;
        this.nome = nome;
        this.codigoBarra = codigoBarra;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
        this.categoria = categoria;
    }

    public Long getIdProdutoBase() { return idProdutoBase; }
    public void setIdProdutoBase(Long idProdutoBase) { this.idProdutoBase = idProdutoBase; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(String codigoBarra) { this.codigoBarra = codigoBarra; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public CategoriaModel getCategoria() { return categoria; }
    public void setCategoria(CategoriaModel categoria) { this.categoria = categoria; }
}
