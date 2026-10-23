package com.example.app_na_esquina.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_produtos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_mercadinho", "id_produto_base"})
})
public class ProdutoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto;

    // Vínculo com o produto global pré-cadastrado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produto_base")
    private ProdutoBaseModel produtoBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mercadinho", nullable = false)
    private MercadinhoModel mercadinho;

    @Column(nullable = false)
    private Double preco;

    @Column(name = "em_destaque")
    private Boolean emDestaque = false;

    @Column(columnDefinition = "boolean default true")
    private Boolean ativo = true;

    // Campos opcionais de sobrescrita (caso o comerciante queira usar nome/foto personalizados)
    private String nomePersonalizado;
    private String imagemUrlPersonalizada;

    public ProdutoModel() {}

    public ProdutoModel(Long idProduto, ProdutoBaseModel produtoBase, MercadinhoModel mercadinho, Double preco, Boolean emDestaque, Boolean ativo) {
        this.idProduto = idProduto;
        this.produtoBase = produtoBase;
        this.mercadinho = mercadinho;
        this.preco = preco;
        this.emDestaque = emDestaque;
        this.ativo = ativo;
    }

    // Métodos utilitários para obter dados unificados (Customizado ou do Catálogo Base)
    public String getNome() {
        return (nomePersonalizado != null && !nomePersonalizado.isBlank())
                ? nomePersonalizado
                : (produtoBase != null ? produtoBase.getNome() : "Produto sem nome");
    }

    public String getImagemUrl() {
        return (imagemUrlPersonalizada != null && !imagemUrlPersonalizada.isBlank())
                ? imagemUrlPersonalizada
                : (produtoBase != null ? produtoBase.getImagemUrl() : null);
    }

    public CategoriaModel getCategoria() {
        return produtoBase != null ? produtoBase.getCategoria() : null;
    }

    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }

    public ProdutoBaseModel getProdutoBase() { return produtoBase; }
    public void setProdutoBase(ProdutoBaseModel produtoBase) { this.produtoBase = produtoBase; }

    public MercadinhoModel getMercadinho() { return mercadinho; }
    public void setMercadinho(MercadinhoModel mercadinho) { this.mercadinho = mercadinho; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Boolean getEmDestaque() { return emDestaque; }
    public void setEmDestaque(Boolean emDestaque) { this.emDestaque = emDestaque; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getNomePersonalizado() { return nomePersonalizado; }
    public void setNomePersonalizado(String nomePersonalizado) { this.nomePersonalizado = nomePersonalizado; }

    public String getImagemUrlPersonalizada() { return imagemUrlPersonalizada; }
    public void setImagemUrlPersonalizada(String imagemUrlPersonalizada) { this.imagemUrlPersonalizada = imagemUrlPersonalizada; }
}