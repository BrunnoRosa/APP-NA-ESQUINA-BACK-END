package com.example.app_na_esquina.dto.avaliacao;

import com.example.app_na_esquina.model.AvaliacaoModel;
import java.time.LocalDateTime;

public class AvaliacaoResponseDTO {

    private Long idAvaliacao;
    private Long idPedido;
    private String nomeCliente;
    private Integer notaAtendimento;
    private Integer notaProdutos;
    private Integer notaLocalizacao;
    private Integer notaAgilidade;
    private Integer notaCustoBeneficio;
    private Double notaGeral;
    private String comentario;
    private LocalDateTime dataAvaliacao;

    public static AvaliacaoResponseDTO de(AvaliacaoModel a) {
        AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO();
        dto.setIdAvaliacao(a.getIdAvaliacao());
        dto.setIdPedido(a.getPedido() != null ? a.getPedido().getIdPedido() : null);
        dto.setNomeCliente(a.getCliente() != null ? a.getCliente().getNome() : "Cliente");
        dto.setNotaAtendimento(a.getNotaAtendimento());
        dto.setNotaProdutos(a.getNotaProdutos());
        dto.setNotaLocalizacao(a.getNotaLocalizacao());
        dto.setNotaAgilidade(a.getNotaAgilidade());
        dto.setNotaCustoBeneficio(a.getNotaCustoBeneficio());
        dto.setNotaGeral(a.getNotaGeral());
        dto.setComentario(a.getComentario());
        dto.setDataAvaliacao(a.getDataAvaliacao());
        return dto;
    }

    public Long getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Long idAvaliacao) { this.idAvaliacao = idAvaliacao; }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

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