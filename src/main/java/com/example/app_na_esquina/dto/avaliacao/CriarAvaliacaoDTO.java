package com.example.app_na_esquina.dto.avaliacao;

public class CriarAvaliacaoDTO {

    private Long idPedido;
    private Integer notaAtendimento;
    private Integer notaProdutos;
    private Integer notaLocalizacao;
    private Integer notaAgilidade;
    private Integer notaCustoBeneficio;
    private String comentario;

    public CriarAvaliacaoDTO() {
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

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

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
