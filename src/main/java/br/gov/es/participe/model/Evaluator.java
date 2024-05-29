package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public abstract class Evaluator extends Entity {

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

}
