package br.gov.es.participe.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnitRolesDto {
    
    @JsonProperty("Guid")
    private String guid;

    @JsonProperty("Nome")
    private String nome;

    @JsonProperty("Tipo")
    private String tipo;

    @JsonProperty("LotacaoGuid")
    private String lotacaoGuid;

    @JsonProperty("AgentePublicoSub")
    private String agentePublicoSub;

    @JsonProperty("AgentePublicoNome")
    private String agentePublicoNome;

    @JsonProperty("Prioritario")
    private boolean prioritario;

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLotacaoGuid() {
        return lotacaoGuid;
    }

    public void setLotacaoGuid(String lotacaoGuid) {
        this.lotacaoGuid = lotacaoGuid;
    }

    public String getAgentePublicoSub() {
        return agentePublicoSub;
    }

    public void setAgentePublicoSub(String agentePublicoSub) {
        this.agentePublicoSub = agentePublicoSub;
    }

    public String getAgentePublicoNome() {
        return agentePublicoNome;
    }

    public void setAgentePublicoNome(String agentePublicoNome) {
        this.agentePublicoNome = agentePublicoNome;
    }

    public boolean isPrioritario() {
        return prioritario;
    }

    public void setPrioritario(boolean prioritario) {
        this.prioritario = prioritario;
    }
    
}
