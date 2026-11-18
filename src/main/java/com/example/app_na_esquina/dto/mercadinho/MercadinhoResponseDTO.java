package com.example.app_na_esquina.dto.mercadinho;

import java.time.LocalTime;

public class MercadinhoResponseDTO {

    private Long idMercadinho;
    private String nomeFantasia;
    private String endereco;
    private String cep;

    private String rua;
    private String numero;
    private String bairro;
    private String complemento;
    private String cidade;
    private String uf;

    private String telefone;
    private Double latitude;
    private Double longitude;

    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;

    private Boolean abreSegSex;
    private LocalTime horarioAberturaSegSex;
    private LocalTime horarioFechamentoSegSex;

    private Boolean abreSabado;
    private LocalTime horarioAberturaSabado;
    private LocalTime horarioFechamentoSabado;

    private Boolean abreDomingo;
    private LocalTime horarioAberturaDomingo;
    private LocalTime horarioFechamentoDomingo;

    public MercadinhoResponseDTO() {
    }

    public Long getIdMercadinho() { return idMercadinho; }
    public void setIdMercadinho(Long idMercadinho) { this.idMercadinho = idMercadinho; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalTime getHorarioAbertura() { return horarioAbertura; }
    public void setHorarioAbertura(LocalTime horarioAbertura) { this.horarioAbertura = horarioAbertura; }

    public LocalTime getHorarioFechamento() { return horarioFechamento; }
    public void setHorarioFechamento(LocalTime horarioFechamento) { this.horarioFechamento = horarioFechamento; }

    public Boolean getAbreSegSex() { return abreSegSex; }
    public void setAbreSegSex(Boolean abreSegSex) { this.abreSegSex = abreSegSex; }

    public LocalTime getHorarioAberturaSegSex() { return horarioAberturaSegSex; }
    public void setHorarioAberturaSegSex(LocalTime horarioAberturaSegSex) { this.horarioAberturaSegSex = horarioAberturaSegSex; }

    public LocalTime getHorarioFechamentoSegSex() { return horarioFechamentoSegSex; }
    public void setHorarioFechamentoSegSex(LocalTime horarioFechamentoSegSex) { this.horarioFechamentoSegSex = horarioFechamentoSegSex; }

    public Boolean getAbreSabado() { return abreSabado; }
    public void setAbreSabado(Boolean abreSabado) { this.abreSabado = abreSabado; }

    public LocalTime getHorarioAberturaSabado() { return horarioAberturaSabado; }
    public void setHorarioAberturaSabado(LocalTime horarioAberturaSabado) { this.horarioAberturaSabado = horarioAberturaSabado; }

    public LocalTime getHorarioFechamentoSabado() { return horarioFechamentoSabado; }
    public void setHorarioFechamentoSabado(LocalTime horarioFechamentoSabado) { this.horarioFechamentoSabado = horarioFechamentoSabado; }

    public Boolean getAbreDomingo() { return abreDomingo; }
    public void setAbreDomingo(Boolean abreDomingo) { this.abreDomingo = abreDomingo; }

    public LocalTime getHorarioAberturaDomingo() { return horarioAberturaDomingo; }
    public void setHorarioAberturaDomingo(LocalTime horarioAberturaDomingo) { this.horarioAberturaDomingo = horarioAberturaDomingo; }

    public LocalTime getHorarioFechamentoDomingo() { return horarioFechamentoDomingo; }
    public void setHorarioFechamentoDomingo(LocalTime horarioFechamentoDomingo) { this.horarioFechamentoDomingo = horarioFechamentoDomingo; }
}