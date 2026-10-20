package com.example.app_na_esquina.model;

import com.example.app_na_esquina.model.enums.CategoriaProduto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}