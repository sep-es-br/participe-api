package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Perfil extends Entity {

    private String descricao;

    public Perfil() {
    }

    public Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
