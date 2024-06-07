package br.gov.es.participe.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganizationUnitsDto {

    @JsonProperty("guid")
    String guid;

    @JsonProperty("nome")
    String nome;

    @JsonProperty("sigla")
    String sigla;

    @JsonProperty("nomeCurto")
    String nomeCurto;

    @JsonProperty("tipoUnidade")
    TipoUnidade tipoUnidade;

    @JsonProperty("unidadePai")
    UnidadePai unidadePai;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNomeCurto() {
        return nomeCurto;
    }

    public void setNomeCurto(String nomeCurto) {
        this.nomeCurto = nomeCurto;
    }

    public TipoUnidade getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoUnidade tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public UnidadePai getUnidadePai() {
        return unidadePai;
    }

    public void setUnidadePai(UnidadePai unidadePai) {
        this.unidadePai = unidadePai;
    }
}

class TipoUnidade {

    @JsonProperty("descricao")
    String descricao;

    @JsonProperty("id")
    int id;
}

class UnidadePai {

    @JsonProperty("guid")
    String guid;

    @JsonProperty("nome")
    String nome;

    @JsonProperty("sigla")
    String sigla;

    @JsonProperty("nomeCurto")
    String nomeCurto;
}

