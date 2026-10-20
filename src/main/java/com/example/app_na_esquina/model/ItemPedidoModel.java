package com.example.app_na_esquina.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tab_itens_pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemPedido;
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonIgnore // Evita recursão infinita na serialização do JSON
    private PedidoModel pedido;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private ProdutoModel produto;

    private Integer quantidade;
    private BigDecimal precoUnitario;
}
