package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Role extends Evaluator {
    
    public Role(String guid) {
        this.setGuid(guid);
    }

}
