/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.dto.acessoCidadao;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author desenvolvimento
 */
public class AcGrupoDto {
    
    @JsonProperty("Guid")
    private String guid;
    
    @JsonProperty("Nome")
    private String nome;
    
    @JsonProperty("ConjuntoPai")
    private String conjuntoPai;
    
    @JsonProperty("TipoNome")
    private String tipoNome;
    
    @JsonProperty("TipoId")
    private Integer tipoId;

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

    public String getConjuntoPai() {
        return conjuntoPai;
    }

    public void setConjuntoPai(String conjuntoPai) {
        this.conjuntoPai = conjuntoPai;
    }

    public String getTipoNome() {
        return tipoNome;
    }

    public void setTipoNome(String tipoNome) {
        this.tipoNome = tipoNome;
    }

    public Integer getTipoId() {
        return tipoId;
    }

    public void setTipoId(Integer tipoId) {
        this.tipoId = tipoId;
    }

    
    
    
}
