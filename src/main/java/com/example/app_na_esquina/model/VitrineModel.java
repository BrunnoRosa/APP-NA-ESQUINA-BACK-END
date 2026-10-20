package com.example.app_na_esquina.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_vitrine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
