package com.example.app_na_esquina.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_avaliacoes")
public class AvaliacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAvaliacao;

    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private PedidoModel pedido;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private UsuarioModel cliente;

    @ManyToOne
    @JoinColumn(name = "id_mercadinho", nullable = false)
    private MercadinhoModel mercadinho;

    @Column(nullable = false)
    private Integer notaAtendimento;

    @Column(nullable = false)
    private Integer notaProdutos;

    @Column(nullable = false)
    private Integer notaLocalizacao;

    @Column(nullable = false)
    private Integer notaAgilidade;

    @Column(nullable = false)
    private Integer notaCustoBeneficio;

    @Column(nullable = false)
    private Double notaGeral;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime dataAvaliacao = LocalDateTime.now();

    public AvaliacaoModel() {
    }

    public Long getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Long idAvaliacao) { this.idAvaliacao = idAvaliacao; }

    public PedidoModel getPedido() { return pedido; }
    public void setPedido(PedidoModel pedido) { this.pedido = pedido; }

    public UsuarioModel getCliente() { return cliente; }
    public void setCliente(UsuarioModel cliente) { this.cliente = cliente; }

    public MercadinhoModel getMercadinho() { return mercadinho; }
    public void setMercadinho(MercadinhoModel mercadinho) { this.mercadinho = mercadinho; }

    public Integer getNotaAtendimento() { return notaAtendimento; }
    public void setNotaAtendimento(Integer notaAtendimento) { this.notaAtendimento = notaAtendimento; }

    public Integer getNotaProdutos() { return notaProdutos; }
    public void setNotaProdutos(Integer notaProdutos) { this.notaProdutos = notaProdutos; }

    public Integer getNotaLocalizacao() { return notaLocalizacao; }
    public void setNotaLocalizacao(Integer notaLocalizacao) { this.notaLocalizacao = notaLocalizacao; }

    public Integer getNotaAgilidade() { return notaAgilidade; }
    public void setNotaAgilidade(Integer notaAgilidade) { this.notaAgilidade = notaAgilidade; }

    public Integer getNotaCustoBeneficio() { return notaCustoBeneficio; }
    public void setNotaCustoBeneficio(Integer notaCustoBeneficio) { this.notaCustoBeneficio = notaCustoBeneficio; }

    public Double getNotaGeral() { return notaGeral; }
    public void setNotaGeral(Double notaGeral) { this.notaGeral = notaGeral; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
}