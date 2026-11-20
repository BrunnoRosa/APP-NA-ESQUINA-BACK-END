package com.example.app_na_esquina.dto.avaliacao;

import java.util.List;

public class ResumoAvaliacoesMercadinhoDTO {

    private Long idMercadinho;
    private int totalAvaliacoes;
    private double mediaGeral;
    private double mediaAtendimento;
    private double mediaProdutos;
    private double mediaLocalizacao;
    private double mediaAgilidade;
    private double mediaCustoBeneficio;
    private List<AvaliacaoResponseDTO> avaliacoes;

    public Long getIdMercadinho() { return idMercadinho; }
    public void setIdMercadinho(Long idMercadinho) { this.idMercadinho = idMercadinho; }

    public int getTotalAvaliacoes() { return totalAvaliacoes; }
    public void setTotalAvaliacoes(int totalAvaliacoes) { this.totalAvaliacoes = totalAvaliacoes; }

    public double getMediaGeral() { return mediaGeral; }
    public void setMediaGeral(double mediaGeral) { this.mediaGeral = mediaGeral; }

    public double getMediaAtendimento() { return mediaAtendimento; }
    public void setMediaAtendimento(double mediaAtendimento) { this.mediaAtendimento = mediaAtendimento; }

    public double getMediaProdutos() { return mediaProdutos; }
    public void setMediaProdutos(double mediaProdutos) { this.mediaProdutos = mediaProdutos; }

    public double getMediaLocalizacao() { return mediaLocalizacao; }
    public void setMediaLocalizacao(double mediaLocalizacao) { this.mediaLocalizacao = mediaLocalizacao; }

    public double getMediaAgilidade() { return mediaAgilidade; }
    public void setMediaAgilidade(double mediaAgilidade) { this.mediaAgilidade = mediaAgilidade; }

    public double getMediaCustoBeneficio() { return mediaCustoBeneficio; }
    public void setMediaCustoBeneficio(double mediaCustoBeneficio) { this.mediaCustoBeneficio = mediaCustoBeneficio; }

    public List<AvaliacaoResponseDTO> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<AvaliacaoResponseDTO> avaliacoes) { this.avaliacoes = avaliacoes; }
}