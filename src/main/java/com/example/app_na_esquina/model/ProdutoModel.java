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
    // Foto enviada pelo comerciante (data URI base64) ou URL: precisa de coluna grande
    @Column(columnDefinition = "LONGTEXT")
    private String imagemUrlPersonalizada;

    // Categoria escolhida pelo comerciante em produto avulso (sem produto do catálogo)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria_personalizada")
    private CategoriaModel categoriaPersonalizada;

    @Column(name = "em_promocao")
    private Boolean emPromocao = false;

    @Column(name = "preco_promocional")
    private Double precoPromocional;

    // Retorna o preço promocional se a promoção estiver ativa e válida; caso contrário, o preço normal
    public Double getPrecoVigente() {
        if (Boolean.TRUE.equals(emPromocao)
                && precoPromocional != null
                && precoPromocional > 0
                && preco != null
                && precoPromocional < preco) {
            return precoPromocional;
        }
        return preco;
    }

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
        if (categoriaPersonalizada != null) return categoriaPersonalizada;
        return produtoBase != null ? produtoBase.getCategoria() : null;
    }

    public ProdutoModel() {
    }

    public ProdutoModel(Long idProduto, ProdutoBaseModel produtoBase, MercadinhoModel mercadinho, Double preco, Boolean emDestaque, Boolean ativo, String nomePersonalizado, String imagemUrlPersonalizada, CategoriaModel categoriaPersonalizada, Boolean emPromocao, Double precoPromocional) {
        this.idProduto = idProduto;
        this.produtoBase = produtoBase;
        this.mercadinho = mercadinho;
        this.preco = preco;
        this.emDestaque = emDestaque;
        this.ativo = ativo;
        this.nomePersonalizado = nomePersonalizado;
        this.imagemUrlPersonalizada = imagemUrlPersonalizada;
        this.categoriaPersonalizada = categoriaPersonalizada;
        this.emPromocao = emPromocao;
        this.precoPromocional = precoPromocional;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public ProdutoBaseModel getProdutoBase() {
        return produtoBase;
    }

    public void setProdutoBase(ProdutoBaseModel produtoBase) {
        this.produtoBase = produtoBase;
    }

    public MercadinhoModel getMercadinho() {
        return mercadinho;
    }

    public void setMercadinho(MercadinhoModel mercadinho) {
        this.mercadinho = mercadinho;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
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

    public String getNomePersonalizado() {
        return nomePersonalizado;
    }

    public void setNomePersonalizado(String nomePersonalizado) {
        this.nomePersonalizado = nomePersonalizado;
    }

    public String getImagemUrlPersonalizada() {
        return imagemUrlPersonalizada;
    }

    public void setImagemUrlPersonalizada(String imagemUrlPersonalizada) {
        this.imagemUrlPersonalizada = imagemUrlPersonalizada;
    }

    public CategoriaModel getCategoriaPersonalizada() {
        return categoriaPersonalizada;
    }

    public void setCategoriaPersonalizada(CategoriaModel categoriaPersonalizada) {
        this.categoriaPersonalizada = categoriaPersonalizada;
    }

    public Boolean getEmPromocao() {
        return emPromocao;
    }

    public void setEmPromocao(Boolean emPromocao) {
        this.emPromocao = emPromocao;
    }

    public Double getPrecoPromocional() {
        return precoPromocional;
    }

    public void setPrecoPromocional(Double precoPromocional) {
        this.precoPromocional = precoPromocional;
    }
}